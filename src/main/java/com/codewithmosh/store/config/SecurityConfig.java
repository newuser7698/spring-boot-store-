package com.codewithmosh.store.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // this class is used to hash the password
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    // This code is using the setUserDetailsService even i do not but it in the func
    // And uses the PasswordEncoder

    // in this func we do not access the provider but only the provider knows where to get the email
    // so we use the provider but how ?
    // actually this code is deprecated we now can make the AuthenticationManager with no need for provider
    // and if we have two managers we can set the security and UserDetail to the manager directly from java IOC
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Stateless sections ( Token based authentication )
        // Disable CSRF -> it is an attack where the browser make a request without the user knowledge
        // Authorized Http requests (public or private)
        // here we tell java not sessions on the server this method throws so we make the func throws

        http
            .sessionManagement(c ->
                    c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // do not use session
            .csrf(AbstractHttpConfigurer::disable) // disable the csrf protection (not needed)
            .authorizeHttpRequests(c ->
                    c
                     .requestMatchers("/carts/**").permitAll() // make any request with cart public
                     .requestMatchers(HttpMethod.POST,"/users").permitAll() // register user is public
                     .requestMatchers(HttpMethod.POST,"/auth/login").permitAll()
                      .anyRequest().authenticated() // make all other requests private
            );

        return http.build();
    }
}
