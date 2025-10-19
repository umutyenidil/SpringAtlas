package com.umutyenidil.atlas.exception;


import com.umutyenidil.atlas.dto.ErrorDetail;
import com.umutyenidil.atlas.dto.ErrorResponse;
import com.umutyenidil.atlas.util.MessageUtil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageUtil messageUtil;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.INTERNAL)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.common.internalserver")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<ErrorDetail> errorDetails = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ErrorDetail.builder()
                        .type(ErrorDetail.Type.VALIDATION)
                        .subject(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build()
                )
                .toList();

        ErrorResponse response = new ErrorResponse(errorDetails);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.BAD_REQUEST)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.body.missing")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(SingleValidationException.class)
    public ResponseEntity<ErrorResponse> handleSingleException(SingleValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.VALIDATION)
                                                .subject(ex.getSubject())
                                                .message(ex.isLocalized() ? messageUtil.getMessage(ex.getMessage()) : ex.getMessage())
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.AUTH)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.auth.badcredentials")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.AUTH)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.common.auth.unauthorized")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.INTERNAL)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.common.noresourcefound")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.AUTH)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.jwt.malformed")))
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(SingleException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(SingleException exp) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(exp.getType())
                                                .subject(exp.getSubject())
                                                .message(exp.isLocalized() ? messageUtil.getMessage(exp.getMessage()) : exp.getMessage())
                                                .build()
                                ))
                                .build()
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .timestamp(Instant.now())
                                .success(false)
                                .errors(List.of(
                                        ErrorDetail.builder()
                                                .type(ErrorDetail.Type.AUTH)
                                                .subject("server")
                                                .message(messageUtil.getMessage(("error.jwt.invalid")))
                                                .build()
                                ))
                                .build()
                );
    }
}

