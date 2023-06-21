package com.rowland.engineering.ecommerce.service;

import com.rowland.engineering.ecommerce.dto.DepositRequest;
import com.rowland.engineering.ecommerce.dto.TransferRequest;
import com.rowland.engineering.ecommerce.dto.UpdateUserRequest;
import com.rowland.engineering.ecommerce.exception.BadRequestException;
import com.rowland.engineering.ecommerce.exception.InsufficientFundException;
import com.rowland.engineering.ecommerce.exception.ReceiverNotFoundException;
import com.rowland.engineering.ecommerce.model.Favourite;

import com.rowland.engineering.ecommerce.model.Transaction;
import com.rowland.engineering.ecommerce.model.TransactionType;
import com.rowland.engineering.ecommerce.model.User;
import com.rowland.engineering.ecommerce.repository.FavouriteRepository;
import com.rowland.engineering.ecommerce.repository.TransactionRepository;
import com.rowland.engineering.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final FavouriteRepository favouriteRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
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
                    user.setAccountBalance(user.getAccountBalance());
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
                    user.setDateOfBirth(user.getDateOfBirth());
                    user.setAccountBalance(user.getAccountBalance() + depositRequest.getDepositAmount());
                    user.setPassword(user.getPassword());
                    return userRepository.save(user);
                }
        );

    }


    @Transactional
    public void makeTransfer(TransferRequest transferRequest, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UsernameNotFoundException("Sender user not found"));

        User receiver = userRepository.findByEmailOrAccountNumber(transferRequest.getEmailOrAccountNumber(), transferRequest.getEmailOrAccountNumber());
        if (receiver == null) {
            throw new ReceiverNotFoundException("Receiver not found for the given email or account number: " + transferRequest.getEmailOrAccountNumber());
        }

        if (transferRequest.getTransferAmount() > sender.getAccountBalance()) {
            throw new InsufficientFundException(transferRequest.getTransferAmount() - sender.getAccountBalance());
        }

        double transferAmount = transferRequest.getTransferAmount();

        double senderBalance = sender.getAccountBalance();
        sender.setAccountBalance(senderBalance - transferAmount);

        double receiverBalance = receiver.getAccountBalance();
        receiver.setAccountBalance(receiverBalance + transferAmount);

        Date timestamp = new Date();

        // Create transaction entities for sender and receiver
        Transaction senderTransaction = new Transaction();
        senderTransaction.setSender(sender);
        senderTransaction.setReceiver(receiver);
        senderTransaction.setAmount(-transferAmount); // Negative value for debit transaction
        senderTransaction.setTransactionType(TransactionType.DEBIT);
        senderTransaction.setTransactionTime(timestamp);

        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setSender(sender);
        receiverTransaction.setReceiver(receiver);
        receiverTransaction.setAmount(transferAmount); // Positive value for credit transaction
        receiverTransaction.setTransactionType(TransactionType.CREDIT);
        receiverTransaction.setTransactionTime(timestamp);

        // Save the updated sender, receiver, and transactions
        userRepository.save(sender);
        userRepository.save(receiver);
        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
    }

    public List<Transaction> viewMyTransactions(Long userId) {
        return transactionRepository.findAllBySenderId(userId);
    }




}
