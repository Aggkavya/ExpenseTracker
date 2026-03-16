package com.personal.Expense_Tracker.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GetTotalAmountExpense {
    private BigDecimal totalExpense;
    private BigDecimal totalCashExpense;
    private BigDecimal totalOnlineExpense;
}
