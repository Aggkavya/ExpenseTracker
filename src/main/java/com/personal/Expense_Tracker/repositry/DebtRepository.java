package com.personal.Expense_Tracker.repositry;


import com.personal.Expense_Tracker.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByUserId(Long userId);
    List<Debt> findByUserIdOrderByDateDesc(Long userId);
}
