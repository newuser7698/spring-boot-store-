package com.codewithmosh.store.Controller;

import com.codewithmosh.store.DTO.ChangePasswordRequest;
import com.codewithmosh.store.DTO.RegisterUserRequest;
import com.codewithmosh.store.DTO.UpdateUserRequest;
import com.codewithmosh.store.DTO.UserDto;
import com.codewithmosh.store.Mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserConroller {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;

    @GetMapping("")
    public Iterable<UserDto> users(@RequestParam(required = false, defaultValue = "", name = "sort") String sortBy) {
        if (!Set.of("name", "email").contains(sortBy)) sortBy = "id";
        return userMapper.toDtoList(userRepository.findAll(Sort.by(sortBy)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> user(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("")
    public ResponseEntity<?> RegisterUser(
            @Valid @RequestBody RegisterUserRequest requesr,
            UriComponentsBuilder uriBuilder
    ) {
        var user = userMapper.toEntity(requesr);

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email is already registered."));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        var userDto = userMapper.toDto(user);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(name = "id") Long id, @RequestBody UpdateUserRequest requesr) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.update(requesr, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> updatePassword(@PathVariable(name = "id") Long id, @RequestBody ChangePasswordRequest requesr) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (!user.getPassword().equals(requesr.getOldPassword())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(requesr.getNewPassword());
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

}

