package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreateDebtResponse {

    private BigDecimal amount;
    private PaymentMode paymentMode;
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;
    private Date date;
    private String description;
    private Boolean isHistorical;

}
