package com.umutyenidil.atlas.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Map<String, Object>> validateToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("http://AUTH-SERVICE/api/v1/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(
                        status -> status != null && status.is4xxClientError(),
                        response -> {

                            if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                                return Mono.error(new RuntimeException("Invalid token"));
                            }

                            return Mono.error(new RuntimeException("Client error during token validation"));
                        })
                .onStatus(
                        status -> status != null && status.is5xxServerError(),
                        response ->
                                Mono.error(new RuntimeException("Auth-service is currently unavailable"))
                )
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
