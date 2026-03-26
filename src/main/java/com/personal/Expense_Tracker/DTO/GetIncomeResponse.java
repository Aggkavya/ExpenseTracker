package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GetIncomeResponse {
    private BigDecimal amount;
    private Long id;
    private String description;



    private PaymentMode paymentMode;
    private Date date;
}
