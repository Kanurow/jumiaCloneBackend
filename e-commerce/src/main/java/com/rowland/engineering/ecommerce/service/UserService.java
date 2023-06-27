package com.rowland.engineering.ecommerce.service;

import com.rowland.engineering.ecommerce.dto.DepositRequest;
import com.rowland.engineering.ecommerce.dto.UpdateUserRequest;
import com.rowland.engineering.ecommerce.exception.BadRequestException;
import com.rowland.engineering.ecommerce.model.Favourite;

import com.rowland.engineering.ecommerce.model.User;
import com.rowland.engineering.ecommerce.repository.FavouriteRepository;
import com.rowland.engineering.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FavouriteRepository favouriteRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }


    public Optional<User> updateUserById(UpdateUserRequest update, Long userId) {
        return userRepository.findById(userId).map(
                user -> {
                    user.setUsername(update.getUsername());
                    user.setFirstName(update.getFirstName());
                    user.setLastName(update.getLastName());
                    user.setEmail(update.getEmail());
                    user.setMobile(update.getMobile());
                    user.setDateOfBirth(update.getDateOfBirth());
                    user.setJumiaAccountNumber(user.getJumiaAccountNumber());
                    user.setPassword(user.getPassword());
                    return userRepository.save(user);
                }
        );
    }

    public void deleteUserById(Long id) {
        List<Favourite> userFavouriteIds = favouriteRepository.findAllByUserId(id);
        favouriteRepository.deleteAll(userFavouriteIds);
        userRepository.deleteById(id);
    }

    public void makeDeposit(DepositRequest depositRequest,Long userId) {

        Optional<User> userAccount = userRepository.findById(userId);
        System.out.println(userAccount);
        if (depositRequest.getDepositAmount() < 1) {
            throw new BadRequestException("Sorry! You cannot deposit a negative amount or zero");
        }
        userAccount.map(
                user -> {
                    user.setUsername(user.getUsername());
                    user.setFirstName(user.getFirstName());
                    user.setLastName(user.getLastName());
                    user.setEmail(user.getEmail());
                    user.setMobile(user.getMobile());
                    user.setJumiaAccountNumber(user.getJumiaAccountNumber());
                    user.setDateOfBirth(user.getDateOfBirth());
                    user.setJumiaAccountNumber(user.getJumiaAccountNumber() + depositRequest.getDepositAmount());
                    user.setPassword(user.getPassword());
                    return userRepository.save(user);
                }
        );

    }





}
