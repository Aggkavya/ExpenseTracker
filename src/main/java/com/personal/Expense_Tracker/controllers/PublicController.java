package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.CreateNewUserRequest;
import com.personal.Expense_Tracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")

public class PublicController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody CreateNewUserRequest newUser) {
        return ResponseEntity.ok(userService.createNewUser(newUser));
    }

}
