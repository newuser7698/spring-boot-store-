package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.User;
import com.codewithmosh.store.repositories.UserRepository;
import io.jsonwebtoken.impl.security.EdwardsCurve;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
       var authentication = SecurityContextHolder.getContext().getAuthentication();
       var id = (Long) authentication.getPrincipal(); // get principle is return anything from type Object

       return userRepository.findById(id).orElse(null);
   }

}
