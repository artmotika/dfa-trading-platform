package org.artmotika.apigatewayservice.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.artmotika.common.dto.KycStatus;
import org.artmotika.common.dto.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = jwtService.extractClaim(jwt, c -> c);
                if (claims != null && jwtService.isTokenValid(jwt)) {
                    UserDto user = UserDto.builder()
                            .id(claims.getSubject())
                            .walletAddress(claims.get("wallet", String.class))
                            .kycStatus(KycStatus.valueOf(claims.get("kycStatus", String.class)))
                            .amlRiskScore(claims.get("amlRiskScore", Integer.class))
                            .qualified(claims.get("qualified", Boolean.class))
                            .frozen(claims.get("frozen", Boolean.class))
                            .build();

                    String role = claims.get("role", String.class);
                    List<GrantedAuthority> authorities = role != null ? 
                            Collections.singletonList(new SimpleGrantedAuthority(role)) : 
                            Collections.emptyList();
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token validation failed
        }
        filterChain.doFilter(request, response);
    }
}
