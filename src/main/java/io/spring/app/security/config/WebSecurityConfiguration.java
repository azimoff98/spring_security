package io.spring.app.security.config;

import io.spring.app.security.controller.JwtAuthenticationEntryPoint;
import io.spring.app.security.controller.JwtAuthorizationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static io.spring.app.security.utils.Constants.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private JwtAuthenticationEntryPoint unauthorizedHandler;


    private JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;

    private String tokenHeader = HEADER;

    private String authenticationPath = PATH;

    @Autowired
    public void setUnathorizedHandler(JwtAuthenticationEntryPoint unathorizedHandler) {
        this.unauthorizedHandler = unathorizedHandler;
    }

    @Autowired
    public void setJwtAuthorizationTokenFilter(JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter) {
        this.jwtAuthorizationTokenFilter = jwtAuthorizationTokenFilter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/signin", "signup").permitAll()
                    .antMatchers(HttpMethod.POST, "/chats").hasRole("ADMIN")
                    .antMatchers(HttpMethod.GET, "/chats").hasAnyRole("ADMIN", "USER")
                    .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().frameOptions().sameOrigin().cacheControl();

    }
}
