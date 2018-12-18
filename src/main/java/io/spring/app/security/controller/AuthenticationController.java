package io.spring.app.security.controller;
import io.spring.app.security.service.AuthenticationService;
import org.springframework.web.bind.annotation.RestController;


import static io.spring.app.security.utils.Constants.*;

@RestController
public class AuthenticationController  {

    private String tokenHeader = HEADER;

    private AuthenticationService authenticationService;

}
