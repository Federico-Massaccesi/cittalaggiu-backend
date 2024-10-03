package it.cittalaggiu.gestioneprodotti.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwt;

    @Autowired
    ApplicationUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Processing AuthTokenFilter");

            var header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer")) {
                var token = header.substring(7);
                log.info("Token: {}", token);

                if (!jwt.isTokenValid(token))
                    throw new JwtException("token non valido");

                var username = jwt.getUsernameFromToken(token);
                log.info("Username: {}", username);
                var details = userDetailsService.loadUserByUsername(username);
                log.info("Details: {}", details);

                var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());//VERIFICA LA CORRETTEZZA DELLE CREDENZIALI
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else {
                System.out.println("---------------- No Token");
            }

        } catch (Exception e) {
            log.error("Exception in auth filter", e);
        }
        //CHIAMA A CASCATA TUTTI I FILTRI CREATI
        filterChain.doFilter(request, response);
    }

}

