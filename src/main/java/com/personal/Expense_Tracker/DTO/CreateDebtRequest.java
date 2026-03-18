package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDebtRequest {

    private BigDecimal amount;
    private String description;
    private PaymentMode paymentMode;
    // Set to true if the debt is from before and shouldn't add to current balance
    private Boolean isHistorical;
}
