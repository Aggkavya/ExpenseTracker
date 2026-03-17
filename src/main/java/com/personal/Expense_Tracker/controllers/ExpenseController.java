package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.CreateExpenseRequest;
import com.personal.Expense_Tracker.DTO.CreateExpenseResponse;
import com.personal.Expense_Tracker.DTO.GetExpenseResponse;
import com.personal.Expense_Tracker.DTO.GetTotalAmountExpense;
import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
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

    @GetMapping("/filter")
    public ResponseEntity<?> getAllExpensesByFilter(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<GetExpenseResponse> filteredExpenses = expenseService.getFilteredExpenses(category, startDate, endDate);
        return new ResponseEntity<>(filteredExpenses, HttpStatus.OK);
    }

    @GetMapping("/total")
    public ResponseEntity<?> totalExpenseByFilter(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        GetTotalAmountExpense total = expenseService.getTotalExpenseFilter(category, startDate, endDate);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteExpense(@RequestParam Long expenseId) {
        expenseService.DeleteExpenseById(expenseId);
        return new ResponseEntity<>("Expense deleted successfully", HttpStatus.OK);
    }
}
