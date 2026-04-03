package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.*;
import com.personal.Expense_Tracker.services.ReceivableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receivable")
public class ReceivableController {

    @Autowired
    private ReceivableService receivableService;

    @PostMapping("/newReceivable")
    public ResponseEntity<CreateReceivableResponse> createNewReceivable(@RequestBody CreateReceivableRequest request) {
        CreateReceivableResponse response = receivableService.newReceivable(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/allReceivables")
    public ResponseEntity<List<GetReceivableResponse>> getAllReceivables() {
        List<GetReceivableResponse> allReceivables = receivableService.getAllReceivables();
        return new ResponseEntity<>(allReceivables, HttpStatus.OK);
    }

    @PostMapping("/collect")
    public ResponseEntity<CollectReceivableResponse> collectReceivable(@RequestBody CollectReceivableRequest request) {
        CollectReceivableResponse response = receivableService.collectReceivable(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{receivableId}/history")
    public ResponseEntity<List<GetReceivableHistoryResponse>> getReceivableHistory(@PathVariable Long receivableId) {
        List<GetReceivableHistoryResponse> history = receivableService.getReceivableHistory(receivableId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteReceivable(@RequestParam Long receivableId) {
        receivableService.deleteReceivable(receivableId);
        return new ResponseEntity<>("Receivable deleted successfully", HttpStatus.OK);
    }
}
