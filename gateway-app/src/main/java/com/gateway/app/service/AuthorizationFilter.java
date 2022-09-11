package com.gateway.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.app.model.UserInformation;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config>{
	@Autowired
	private JwtService jwtService;
	
	public AuthorizationFilter() {
		super(Config.class);
	}

	public static class Config {	
		
	}
	
	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		return response.setComplete();
	}
	
	@Override
	public GatewayFilter apply(Config config) {			
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();			
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwtToken = authorizationHeader.replace("Bearer ", "");
			UserInformation userInformation = new UserInformation();
			boolean validateJwtToken = jwtService.validateJwtToken(jwtToken, userInformation);
			if(!validateJwtToken) {
				return onError(exchange, HttpStatus.UNAUTHORIZED);
			}else {
				exchange.getRequest().mutate()
				.header("role", (String)userInformation.getRole())
				.header("username", (String)userInformation.getUserName()).build();
			}
			return chain.filter(exchange);			
		};
	}

	
}

	
