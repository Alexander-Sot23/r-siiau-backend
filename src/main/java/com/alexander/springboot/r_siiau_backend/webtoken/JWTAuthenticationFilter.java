package com.alexander.springboot.r_siiau_backend.webtoken;

import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alexander.springboot.r_siiau_backend.service.details.MyUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.io.IOException;

@Configuration
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JWTService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/api/login") || path.equals("/api/logout");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = null;
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String userName = null;

        try {
            userName = jwtService.extractUserName(jwt);
        } catch (ExpiredJwtException e) {
            //JWT expirado - devolver 401 con mensaje
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = "{\"error\":\"JWT token has expired\",\"type\":\"TOKEN_EXPIRED\"}";
            response.getWriter().write(jsonResponse);
            return; //No continuar con el filtro
        } catch (Exception e) {
            //Otras excepciones de JWT - continuar sin autenticar
            filterChain.doFilter(request, response);
            return;
        }

        if(userName!=null && SecurityContextHolder.getContext().getAuthentication() == null){
            try {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(userName);
                if(userDetails != null && jwtService.isTokenValid(jwt)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,  //MyUserDetails completo
                            null,  //null porque ya verificamos el JWT
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (ExpiredJwtException e) {
                //JWT expirado durante validación - devolver 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                String jsonResponse = "{\"error\":\"JWT token has expired\",\"type\":\"TOKEN_EXPIRED\"}";
                response.getWriter().write(jsonResponse);
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

}