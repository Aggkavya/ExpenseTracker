package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayDebtResponse {

    private Long ledgerId;
    private Long debtId;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private PaymentMode paymentMode;
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;
    private Date paymentDate;
    private String description;

}
