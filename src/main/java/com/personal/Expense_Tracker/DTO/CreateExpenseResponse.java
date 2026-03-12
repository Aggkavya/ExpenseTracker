package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateExpenseResponse {

    private BigDecimal amount;

    private String description;

    private Category category;

    private PaymentMode paymentMode;
    private Date date;
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;

}
