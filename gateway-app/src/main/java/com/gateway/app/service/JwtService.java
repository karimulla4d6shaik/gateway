package com.gateway.app.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.gateway.app.model.UserInformation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	
	@Autowired
	private Environment env;
	public boolean validateJwtToken(String jwtToken, UserInformation userInformation) {
		boolean tokenValidate = false;
		Claims claims = getAllClaims(jwtToken);		
		Date expiration = claims.getExpiration();
		if(expiration.after(new Date())) {
			tokenValidate = true;
			userInformation.setFullname(claims.get("fullname"));
			userInformation.setUserName(claims.get("Username"));
			userInformation.setRole(claims.get("Role"));
		}
		return tokenValidate;
	}
	
	private Claims getAllClaims(String jwtToken) {
		return Jwts.parser().setSigningKey(env.getProperty("security-key")).parseClaimsJws(jwtToken).getBody();
	}
}
