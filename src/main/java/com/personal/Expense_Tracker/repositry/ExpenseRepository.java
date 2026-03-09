package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
