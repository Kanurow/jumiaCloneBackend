package com.rowland.engineering.ecommerce.controller;

import com.rowland.engineering.ecommerce.dto.*;
import com.rowland.engineering.ecommerce.model.User;
import com.rowland.engineering.ecommerce.security.CurrentUser;
import com.rowland.engineering.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User")
public class UserController {

    private final UserService userService;


    @Operation(
            description = "Delete User",
            summary = "Enables admin delete a user from db"
    )
    @DeleteMapping("users/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "id") Long id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(new ApiResponse(true, "User has been deleted"), HttpStatus.ACCEPTED);
    }

    @Operation(
            description = "Get user by Id",
            summary = "Returns user by providing user id"
    )
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable(value = "id") Long id) {
        return userService.findUserById(id);
    }



    @Operation(
            description = "Update user information",
            summary = "Updates user profile information"
    )
    @PutMapping("/update/{id}")
    public Optional<User> updateUserById(@RequestBody UpdateUserRequest update,
                                         @PathVariable(value = "id") Long userId) {
        return userService.updateUserById(update, userId);
    }

    @Operation(
            description = "Get all registered users",
            summary = "Returns all registered users"
    )
    @GetMapping("/all")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }


    @Operation(
            description = "Gets current logged in user's authentication details",
            summary = "Returns logged in user's authentication details"
    )
    @GetMapping("/user/me")
    public UserSummary getCurrentUser(@CurrentUser User currentUser) {
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());
    }

    @Operation(
            description = "Deposit money into user account",
            summary = "Enables money deposit that can be used for purchases"
    )
    @PutMapping("/deposit/{id}")
    public ResponseEntity<?> makeDeposit(
            @RequestBody DepositRequest depositRequest,
            @PathVariable(value = "id") Long id){
        userService.makeDeposit(depositRequest, id);
        return new ResponseEntity<>(new ApiResponse(true, " "+ depositRequest.getDepositAmount() +" has been deposited into your jumia account"), HttpStatus.ACCEPTED);
    }




}
