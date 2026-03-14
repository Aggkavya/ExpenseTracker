package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.CreateExpenseRequest;
import com.personal.Expense_Tracker.DTO.CreateExpenseResponse;
import com.personal.Expense_Tracker.DTO.GetExpenseResponse;
import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.Expense;
import com.personal.Expense_Tracker.repositry.ExpenseRepository;
import com.personal.Expense_Tracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/allExpenses")
    public ResponseEntity<?> getAllExpenses() {
        List<GetExpenseResponse> allExpenses = expenseService.getAllExpenses();
        return new ResponseEntity<>(allExpenses, HttpStatus.OK);

    }

    @GetMapping("/allExpenses/{category}")
    public ResponseEntity<?> getAllExpensesByCategory(@PathVariable Category category) {
        List<GetExpenseResponse> expenses = expenseService.getAllExpensesByCategory(category);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> getAllExpensesByDate() {
        return ResponseEntity.ok(null);
    }
}
