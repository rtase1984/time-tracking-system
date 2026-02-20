package com.timetracking.apigateway.api_gateway.exception;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    log.error("Gateway error: ", ex);

    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    String errorMessage = "{\"error\":\"Internal Server Error\",\"message\":\"" + ex.getMessage() + "\"}";
    byte[] bytes = errorMessage.getBytes(StandardCharsets.UTF_8);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
