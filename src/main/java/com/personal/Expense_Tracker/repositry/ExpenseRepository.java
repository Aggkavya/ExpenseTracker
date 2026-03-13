package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, Category category);
}
