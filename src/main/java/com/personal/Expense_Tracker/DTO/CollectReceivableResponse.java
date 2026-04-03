package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CollectReceivableResponse {

    private Long ledgerId;
    private Long receivableId;
    private BigDecimal collectedAmount;
    private BigDecimal remainingAmount;
    private PaymentMode paymentMode;
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;
    private Date collectionDate;
    private String description;
}
