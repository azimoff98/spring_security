package io.spring.app.security.service;

import io.spring.app.model.User;
import io.spring.app.repository.UserRepository;
import io.spring.app.security.exceptions.AuthenticationException;
import io.spring.app.security.exceptions.TokenNotFoundException;
import io.spring.app.security.model.JwtUser;
import io.spring.app.security.model.dto.JwtAuthenticationRequest;
import io.spring.app.security.model.dto.JwtAuthenticationResponse;
import io.spring.app.security.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {

    private AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    private UserDetailsService userDetailsService;

    private UserRepository userRepository;



    public JwtAuthenticationResponse createAuthenticationToken(JwtAuthenticationRequest request){

        authenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);

        return new JwtAuthenticationResponse(token);

    }

    public JwtAuthenticationResponse refreshToken(String oldToken){
        if(Objects.isNull(oldToken) || oldToken.length() < 7){
            throw new TokenNotFoundException("No old token");
        }

        String token = oldToken.substring(7);

        if(jwtTokenUtil.canTokenBeRefreshed(token)){
            String newToken = jwtTokenUtil.refreshToken(token);
            return new JwtAuthenticationResponse(newToken);
        }

        throw new TokenNotFoundException("Token can/t be refreshed");
    }

    public JwtUser getUserByToken(String authToken){
        if(Objects.isNull(authToken) || authToken.length() < 7){
            throw new TokenNotFoundException("Can't refresh user");
        }

        String token = authToken.substring(7);

        String username = jwtTokenUtil.getUsernameFromToken(token);

        return (JwtUser) userDetailsService.loadUserByUsername(username);
    }

    private void authenticate(String username, String password){
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        }catch (DisabledException e){
            throw new AuthenticationException("User is disabled", e);
        }catch (BadCredentialsException e){
            throw new AuthenticationException("Bad credentials", e);
        }

    }

    public User save(User user){
        return null;
    }


    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    @Qualifier("jwtUserDetailService")
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
