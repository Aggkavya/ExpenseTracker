package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateReceivableRequest {

    private BigDecimal amount;
    private String description;
    private PaymentMode paymentMode;
    // true = receivable is from before the app — do NOT deduct from current balance
    private Boolean isHistorical;
}
