package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.*;
import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.services.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/income")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @PostMapping("/newIncome")
    public ResponseEntity<?> createNewIncome(@RequestBody CreateIncomeRequest incomeRequest) {
        CreateIncomeResponse response = incomeService.newIncome(incomeRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/allIncomes")
    public ResponseEntity<?> getAllIncomes() {
        List<GetIncomeResponse> allIncome = incomeService.getAllIncome();
        return new ResponseEntity<>(allIncome, HttpStatus.OK);

    }
    @GetMapping("/total")
    public ResponseEntity<?> totalIncomeByFilter(

            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        GetTotalAmountIncome total = incomeService.getTotalIncomeFilter( startDate, endDate);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteIncome(@RequestParam Long incomeId) {
        incomeService.DeleteIncomeById(incomeId);
        return new ResponseEntity<>("Income deleted successfully", HttpStatus.OK);
    }
}
