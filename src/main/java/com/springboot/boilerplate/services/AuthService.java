package com.springboot.boilerplate.services;


import com.springboot.boilerplate.entities.User;
import com.springboot.boilerplate.enums.UserType;
import com.springboot.boilerplate.repositories.UserRepository;
import com.springboot.boilerplate.security.JWTUtil;
import com.springboot.boilerplate.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private PasswordEncoder passwordEncoder;



    public Result<String> signin(String email, String password) throws UsernameNotFoundException {

        Optional<User> userRes = userRepository.findFirstByEmail(email);

        if (userRes.isEmpty()) {
            return new Result<>(false, "error", "User not found");
        }

        User user = userRes.get();

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(user.getId(), password);
        authManager.authenticate(authInputToken);
        String token = jwtUtil.generateToken(user.getId());
        System.out.println(token);

        return new Result<>(true, "success", token);


    }


    public Result<String> signup(String email, String password, String name, String surname, String username) throws RuntimeException {

        Optional<User> userRes = userRepository.findFirstByEmail(email);

        if (userRes.isPresent()) {
            System.out.println(userRes.get());
            return new Result<>(false, "error", "User already exists");
        }

        User user = new User();


        String encodedPass = passwordEncoder.encode(password);


        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(encodedPass);
        user.setName(name);
        user.setSurname(surname);
        user.setUserType(UserType.USER);
        userRepository.save(user);

        return new Result<>(true, "success", "User created");

    }
}