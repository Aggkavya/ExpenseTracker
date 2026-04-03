package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReceivableLedgerResponse {

    private Long id;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private String description;
    private Date date;
}
