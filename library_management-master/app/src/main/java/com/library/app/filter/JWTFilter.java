package com.library.app.filter;

import com.library.app.service.JWTService;
import com.library.app.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @Autowired
    private JWTService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headers = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(headers!=null && headers.startsWith("Bearer ")){
            token = headers.substring(7);
            username = jwtService.getUsername(token);
            if(username == null){
                filterChain.doFilter(request, response);
                return;
            }
        }
    if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
        UserDetails user = myUserDetailsService.loadUserByUsername(username);
        try{
            if(jwtService.validationToken(token, username, user)){
                UsernamePasswordAuthenticationToken authtoken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                authtoken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authtoken);
            }
        }catch (Exception e){
            return ;
        }


    }filterChain.doFilter(request, response);
    }
}
