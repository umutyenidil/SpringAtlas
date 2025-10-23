package com.umutyenidil.atlas.security;

import com.umutyenidil.atlas.dto.ErrorDetail;
import com.umutyenidil.atlas.exception.SingleException;
import com.umutyenidil.atlas.service.JwtService;
import com.umutyenidil.atlas.service.RedisBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RedisBlacklistService redisBlacklistService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        try {
            if (request.getServletPath().contains("/api/v1/auth/login") ||
                    request.getServletPath().contains("/api/v1/auth/register") || request.getServletPath().contains("/actuator")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String email;

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new SingleException(
                        ErrorDetail.Type.AUTH,
                        "server",
                        "error.jwt.malformed"
                );
            }

            jwt = authorizationHeader.substring(7);
            email = jwtService.extractEmail(jwt);

            if (redisBlacklistService.contains(jwt)) {
                throw new SingleException(
                        ErrorDetail.Type.AUTH,
                        "server",
                        "error.jwt.invalid"
                );
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            (new WebAuthenticationDetailsSource())
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exp) {
            handlerExceptionResolver.resolveException(request, response, null, exp);
        }
    }
}
