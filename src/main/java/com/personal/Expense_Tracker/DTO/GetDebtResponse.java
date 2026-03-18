package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.PaymentMode;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class GetDebtResponse {

    private Long id;
    private BigDecimal amount;
    private BigDecimal remainingAmount;
    private PaymentMode paymentMode;
    private String description;
    private Date date;
    private Boolean isHistorical;

    private List<DebtLedgerResponse> ledgers;

}
