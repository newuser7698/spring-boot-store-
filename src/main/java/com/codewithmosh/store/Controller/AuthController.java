package com.codewithmosh.store.Controller;

import com.codewithmosh.store.DTO.JwtResponse;
import com.codewithmosh.store.DTO.LoginRequest;
import com.codewithmosh.store.DTO.UserDto;
import com.codewithmosh.store.Mappers.UserMapper;
import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // when make the response we deliver the access token on the response but
        // we save the refresh token in a place that JS can not access in HTTP only cookie

        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh"); // where we can send this cookie the / means the entire webSite but we will make it in the refresh access token end point only
        cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration()); // will expire the cookie at 7 days
        response.addCookie(cookie);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @PostMapping("/refresh")
   public ResponseEntity<JwtResponse> refresh(
          @CookieValue(value = "refreshToken") String refershToken
    ) {
        var jwt = jwtService.parseToken(refershToken);
        if (jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var id = (Long) authentication.getPrincipal(); // get principle is return anything from type Object

        var user = userRepository.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();

        var userDto = userMapper.toDto(user);

        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
