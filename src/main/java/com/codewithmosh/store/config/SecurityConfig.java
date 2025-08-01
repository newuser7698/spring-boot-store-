package com.codewithmosh.store.config;

import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.filters.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    //    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Note: I think this func must not be here we should make it separate from config file
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); // this class is used to hash the password
//    }

    // I think this func should not be here
    //
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        var provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService);
//        return provider;
//    }

    // This code is using the setUserDetailsService even i do not but it in the func
    // And uses the PasswordEncoder

    // in this func we do not access the provider but only the provider knows where to get the email
    // so we use the provider but how ?
    // actually this code is deprecated we now can make the AuthenticationManager with no need for provider
    // and if we have two managers we can set the security and UserDetail to the manager directly from java IOC
    /* The real implementation is like this
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return builder.build();
    }
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration conf) throws Exception {
        return conf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
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
                            .requestMatchers("/admin/**").hasRole(Role.ADMIN.name()) // only user with role of ADMIN
                            .requestMatchers(HttpMethod.POST, "/users").permitAll() // register user is public
                            .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                            .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                            .requestMatchers(HttpMethod.POST, "/checkout/webhook").permitAll()
                            .anyRequest().authenticated() // make all other requests private
            )
                // make the filter we made run before any filter
                // so we make it run before the UsernamePasswordAuthenticationFilter.class -> the first one
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // when the access token is expired, and we try to access the api we get forbidden but this is not true
                // we want to get unauthorized better
                // we change the default one
                // so if the client try to access any protected and point will get unauthorized error

//        This configures how Spring Security handles exceptions
                .exceptionHandling(c -> {
//        If a request is unauthenticated (user not logged in),
                c.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
//        This is what happens when the user is authenticated, but not allowed to access the resource.
                ExceptionHandlingConfigurer<HttpSecurity> httpSecurityExceptionHandlingConfigurer = c.accessDeniedHandler((request, response, accessDeniedException) ->
                        response.setStatus(HttpStatus.FORBIDDEN.value()));
            })
        ;

        return http.build();
    }
}
