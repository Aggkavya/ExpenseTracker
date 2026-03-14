package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);

    List<Expense> findByUserIdOrderByDateDesc(Long userId);

    List<Expense> findByUserIdAndCategoryOrderByDateDesc(Long userId, Category category);

    List<Expense> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, java.util.Date startDate, java.util.Date endDate);
    // understand them
    @Query("SELECT e FROM Expense e where e.user.id = :userId " +
            " AND (cast(:category as string) IS NULL OR e.category = :category) " +
            " AND (cast(:startDate as timestamp) IS NULL OR e.date >= :startDate) "+
            " AND (cast(:endDate as timestamp) IS NULL OR e.date <= :endDate) "+
            " ORDER BY e.date DESC")
    List<Expense> findFilteredExpenses(@Param("userId") Long userId,
                                       @Param("category") Category category,
                                       @Param("startDate") Date startDate,
                                       @Param("endDate") Date endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e where e.user.id = :userId "+
            " AND (cast(:category as string) IS NULL OR e.category = :category) " +
            " AND (cast(:startDate as timestamp) IS NULL OR e.date >= :startDate) "+
            " AND (cast(:endDate as timestamp) IS NULL OR e.date <= :endDate) ")
    BigDecimal calculateTotalExpense(@Param("userId") Long userId,
                                     @Param("category") Category category,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);


}
