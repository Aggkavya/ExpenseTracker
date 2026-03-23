package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GetDebtHistoryResponse {
    private Long id;
    private BigDecimal amountPaid;
    private PaymentMode paymentMode;
    private Date date;
    private String description;


}
