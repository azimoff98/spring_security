package io.spring.app.security.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.spring.app.security.service.JwtUserDetailsService;
import io.spring.app.security.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static io.spring.app.security.utils.Constants.*;

@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {


    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationTokenFilter.class);


    private UserDetailsService userDetailsService;

    private JwtTokenUtil jwtTokenUtil;


    private String tokenHeader = HEADER;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        log.debug("processing authentication for '{}'", request.getRequestURI());

        String requestHeader = request.getHeader(this.tokenHeader);

        String username = null;
        String authToken = null;

        if(Objects.nonNull(requestHeader) && requestHeader.startsWith("Bearer ")){
            authToken = requestHeader.substring(7);
            try{
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            }catch (IllegalArgumentException e){
                log.error("An error occurred during getting username from token ", e);
            }catch (ExpiredJwtException e){
                log.warn("the token is expired and not valid anymore", e);
            }
        }else{
            log.warn("could not find bearer string, will ignore the header");
        }

        log.debug("checking authentication for user '{}'", username);

        if(Objects.nonNull(username) && SecurityContextHolder.getContext().getAuthentication() == null){
            log.debug("security context was null, so authorizating user");

            // It is not compelling necessary to load the use details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if(jwtTokenUtil.validateToken(authToken, userDetails)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("authorized user '{}', setting security context", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }
        chain.doFilter(request, response);
    }

    @Autowired
    public void setUserDetailsService(JwtUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }
}
