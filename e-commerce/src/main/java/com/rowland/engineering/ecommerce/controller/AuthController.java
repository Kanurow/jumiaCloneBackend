package com.rowland.engineering.ecommerce.controller;

import com.rowland.engineering.ecommerce.exception.AppException;
import com.rowland.engineering.ecommerce.model.PromoCode;
import com.rowland.engineering.ecommerce.model.Role;
import com.rowland.engineering.ecommerce.model.RoleName;
import com.rowland.engineering.ecommerce.model.User;
import com.rowland.engineering.ecommerce.dto.ApiResponse;
import com.rowland.engineering.ecommerce.dto.JwtAuthenticationResponse;
import com.rowland.engineering.ecommerce.dto.LoginRequest;
import com.rowland.engineering.ecommerce.dto.RegisterRequest;
import com.rowland.engineering.ecommerce.repository.PromoCodeRepository;
import com.rowland.engineering.ecommerce.repository.RoleRepository;
import com.rowland.engineering.ecommerce.repository.UserRepository;
import com.rowland.engineering.ecommerce.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication - Registration /Sign In")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final PromoCodeRepository promoCodeRepository;
    private final JwtTokenProvider tokenProvider;

    @Operation(
            description = "Post request for signing in registered user",
            summary = "Enables registered user sign in with either username or email and returns bearer token"
    )
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @Operation(
            description = "Post request for adding user to database",
            summary = "Enables user registration - To sign up with admin role, add `row` to email field."
    )
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody  RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        // Creating user's account
        User user = new User(registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getDateOfBirth(), registerRequest.getUsername(),
                registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getMobile());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String userAccountNumber = generateAccountNumber();
        user.setAccountNumber(userAccountNumber);

        Role userRole;

        if (registerRequest.getEmail().contains("row")) {
            userRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new AppException("Admin Role not set."));
        } else {
            System.out.println("USER");
            userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new AppException("User Role not set."));
        }
        Double openingAccountBalance;
        if(registerRequest.getAccountBalance() == null || registerRequest.getAccountBalance() <= 0) {
            openingAccountBalance = 0.0;
        } else {
            openingAccountBalance = registerRequest.getAccountBalance();
        }

        user.setRoles(Collections.singleton(userRole));
        user.setAccountBalance(openingAccountBalance);
        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(savedUser.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    public static String generateAccountNumber() {
        Random random = new Random();

        // Generate a random integer with 10 digits
        int randomNumber = random.nextInt(9000000) + 10000000;

        // Concatenate with "00"
        String generatedNumber = "00" + String.valueOf(randomNumber);
        System.out.println(generatedNumber);

        return generatedNumber;
    }


}
