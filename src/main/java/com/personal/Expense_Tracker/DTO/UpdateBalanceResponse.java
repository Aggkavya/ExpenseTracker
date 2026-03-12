package com.personal.Expense_Tracker.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpdateBalanceResponse {
    private BigDecimal cashInHand;
    private BigDecimal bankBalance;
}
