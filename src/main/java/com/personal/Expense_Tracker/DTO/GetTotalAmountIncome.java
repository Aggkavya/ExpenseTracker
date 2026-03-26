package com.personal.Expense_Tracker.DTO;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class GetTotalAmountIncome {
    private BigDecimal totalIncome;
    private BigDecimal totalCashIncome;
    private BigDecimal totalOnlineIncome;
}
