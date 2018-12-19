package io.spring.app.security.controller;
import io.spring.app.model.User;
import io.spring.app.security.exceptions.AuthenticationException;
import io.spring.app.security.model.JwtUser;
import io.spring.app.security.model.dto.JwtAuthenticationRequest;
import io.spring.app.security.model.dto.JwtAuthenticationResponse;
import io.spring.app.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;

import static io.spring.app.security.utils.Constants.*;

@RestController
public class AuthenticationController  {

    private String tokenHeader = HEADER;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public User save(@RequestBody User user){
       return authenticationService.save(user);
    }

    @PostMapping("/signin")
    public JwtAuthenticationResponse createAuthenticationToken(@RequestBody JwtAuthenticationRequest request){
        return authenticationService.createAuthenticationToken(request);
    }

    @GetMapping("/signin")
    public JwtAuthenticationResponse refreshAndGetAuthenticationToken(HttpServletRequest request){
        String authToken = request.getHeader(tokenHeader);
        return authenticationService.refreshToken(authToken);
    }

    @GetMapping("/user")
    public JwtUser getAuthenticatedUser(HttpServletRequest request){
        String authToken = request.getHeader(tokenHeader);
        return authenticationService.getUserByToken(authToken);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }





}
