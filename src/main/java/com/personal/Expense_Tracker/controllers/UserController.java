package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.UpdateBalanceRequest;
import com.personal.Expense_Tracker.DTO.UpdateBalanceResponse;
import com.personal.Expense_Tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.Expense_Tracker.repositry.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PutMapping("/updateBalance")
    public ResponseEntity<?> updateBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest) {
        UpdateBalanceResponse response = userService.updateBalance(updateBalanceRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance() {
        UpdateBalanceResponse response = userService.getBalance();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
