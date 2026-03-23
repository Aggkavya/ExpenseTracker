package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.*;
import com.personal.Expense_Tracker.services.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debt")
public class DebtController {
    @Autowired
    private DebtService debtService;

    @PostMapping("/newDebt")
    public ResponseEntity<CreateDebtResponse> createNewDebt(@RequestBody CreateDebtRequest request) {
        CreateDebtResponse response = debtService.newDebt(request);
        return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/allDebts")
    public ResponseEntity<List<GetDebtResponse>> getAllDebts() {
        List<GetDebtResponse> allDebts = debtService.getAllDebts();
        return new ResponseEntity<>(allDebts, HttpStatus.OK);
    }

    @PostMapping("/pay")
    public ResponseEntity<PayDebtResponse> payDebt(@RequestBody PayDebtRequest request) {
        PayDebtResponse response = debtService.payDebt(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDebt(@RequestParam Long debtId) {
        debtService.deleteDebt(debtId);
        return new ResponseEntity<>("Debt and related ledgers deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/{debtId}/history")
    public ResponseEntity<?> debtResponseHistory(@PathVariable Long debtId) {
        List<GetDebtHistoryResponse> history = debtService.getDebtHistory(debtId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}
