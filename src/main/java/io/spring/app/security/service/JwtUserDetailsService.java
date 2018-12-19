package io.spring.app.security.service;

import io.spring.app.repository.UserRepository;
import io.spring.app.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("jwtUserDetailService")
public class JwtUserDetailsService implements UserDetailsService {

    private UserRepository repository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return  repository.findByUsername(username)
                .map(JwtUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }


    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
