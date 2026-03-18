package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayDebtRequest {

    private Long debtId;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String description;

}
