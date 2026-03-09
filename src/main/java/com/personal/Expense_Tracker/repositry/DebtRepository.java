package com.personal.Expense_Tracker.repositry;


import com.personal.Expense_Tracker.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtRepository extends JpaRepository<Debt, Long> {

}
