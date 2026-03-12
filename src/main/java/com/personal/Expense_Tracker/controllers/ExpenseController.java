package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.CreateExpenseRequest;
import com.personal.Expense_Tracker.DTO.CreateExpenseResponse;
import com.personal.Expense_Tracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expense")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/newExpense")
    public ResponseEntity<?> createNewExpense(@RequestBody CreateExpenseRequest expenseRequest) {
        CreateExpenseResponse response = expenseService.newExpense(expenseRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
