package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateIncomeRequest {
    private BigDecimal amount;

    private String description;

    private PaymentMode paymentMode;

}
