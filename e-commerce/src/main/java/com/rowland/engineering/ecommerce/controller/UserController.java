package com.rowland.engineering.ecommerce.controller;

import com.rowland.engineering.ecommerce.dto.*;
import com.rowland.engineering.ecommerce.model.Favourite;
import com.rowland.engineering.ecommerce.model.RoleName;
import com.rowland.engineering.ecommerce.model.Transaction;
import com.rowland.engineering.ecommerce.model.User;
import com.rowland.engineering.ecommerce.repository.UserRepository;
import com.rowland.engineering.ecommerce.security.CurrentUser;
import com.rowland.engineering.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final UserRepository userRepository;


    @Operation(
            description = "Get all created promo code",
            summary = "Enables admin view all created promo codes"
    )
    @DeleteMapping("users/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")  //@RolesAllowed("ROLE_ADMIN")
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
            description = "Gets current logged in user's id and username",
            summary = "Returns logged in user's Id and Username"
    )
    @GetMapping("/user/me")    //@PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser User currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getEmail());
        return userSummary;
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
        return new ResponseEntity<>(new ApiResponse(true, " "+ depositRequest.getDepositAmount() +" has been deposited into your account"), HttpStatus.ACCEPTED);
    }



    @Operation(
            description = "Transfer money into a different users account",
            summary = "Enables money transfer into a different users account using email/account number"
    )
    @PutMapping("/transfer/{senderId}")
    public ResponseEntity<?> transferToUser(
            @RequestBody TransferRequest transferRequest,
            @PathVariable(value = "senderId") Long senderId){
        userService.makeTransfer(transferRequest, senderId);
        return new ResponseEntity<>(new ApiResponse(true, " You have successfully transferred #"+ transferRequest.getTransferAmount() +" to " + transferRequest.getEmailOrAccountNumber()), HttpStatus.ACCEPTED);
    }




    @Operation(
            description = "Gets users money transaction history",
            summary = "View user transaction history ie Debits and Credits"
    )
    @GetMapping("/transactionhistory/{userId}")
    public List<Transaction> viewMyTransactions(
            @PathVariable(value = "userId") Long userId){
        return userService.viewMyTransactions(userId);
//        return new ResponseEntity<>(new ApiResponse(true, " You have successfully transferred #"+ transferRequest.getTransferAmount() +" to " + transferRequest.getEmailOrAccountNumber()), HttpStatus.ACCEPTED);
    }

}
