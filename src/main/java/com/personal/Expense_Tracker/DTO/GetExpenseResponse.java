package com.personal.Expense_Tracker.DTO;

import java.math.BigDecimal;
import java.util.Date;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.PaymentMode;

import lombok.Data;

@Data
public class GetExpenseResponse {
    private BigDecimal amount;
    private Long id;
    private String description;

    private Category category;

    private PaymentMode paymentMode;
    private Date date;
}
