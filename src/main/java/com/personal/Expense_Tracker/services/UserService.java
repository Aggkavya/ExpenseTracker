package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.CreateNewUserRequest;
import com.personal.Expense_Tracker.DTO.CreateNewUserResponse;
import com.personal.Expense_Tracker.DTO.UpdateBalanceRequest;
import com.personal.Expense_Tracker.DTO.UpdateBalanceResponse;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.personal.Expense_Tracker.entity.ROLE.ROLE_USER;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

@Service
public class UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public CreateNewUserResponse createNewUser(CreateNewUserRequest newUser) {
        User user = mapToUser(newUser);
        User save = userRepository.save(user);
        CreateNewUserResponse returnUser = new CreateNewUserResponse();
        returnUser.setName(save.getName());
        return returnUser;
    }

    public UpdateBalanceResponse updateBalance(UpdateBalanceRequest balanceRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);

        //check for null balance
        if(balanceRequest.getBankBalance() != null){
            user.setBankBalance(balanceRequest.getBankBalance());
        }
        if(balanceRequest.getCashInHand() != null){
            user.setCashInHand(balanceRequest.getCashInHand());
        }
        //save user
        userRepository.save(user);
        //map to response
        UpdateBalanceResponse response = new UpdateBalanceResponse();
        response.setBankBalance(user.getBankBalance());
        response.setCashInHand(user.getCashInHand());
        return response;

    }

    private User mapToUser(CreateNewUserRequest newUser) {
        if (newUser == null) {
            return null;
        }
        User user = new User();
        user.setUserName(newUser.getUserName());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setEmail(newUser.getEmail());
        user.setName(newUser.getName());
        user.setDebt(BigDecimal.ZERO);
        user.setAllDebts(new ArrayList<>());
        user.setAllExpenses(new ArrayList<>());
        user.setBankBalance(BigDecimal.ZERO);
        user.setCashInHand(BigDecimal.ZERO);
        user.setRoles(new HashSet<>(Collections.singleton(ROLE_USER)));
        return user;
    }

}
