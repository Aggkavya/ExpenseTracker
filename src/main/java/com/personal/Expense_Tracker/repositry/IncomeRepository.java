package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.Income;
import com.personal.Expense_Tracker.entity.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
    List<Income> findByUserIdOrderByDateDesc(Long userId);
    @Query("SELECT SUM(e.amount) FROM Income e where e.user.id = :userId "+
            " AND (cast(:startDate as timestamp) IS NULL OR e.date >= :startDate) "+
            " AND (cast(:endDate as timestamp) IS NULL OR e.date <= :endDate) ")
    BigDecimal calculateTotalIncome(@Param("userId") Long userId,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);

    // 3. Combined method for Total by specific Payment Mode (Replaces the two separate ones)
    @Query("SELECT SUM(e.amount) FROM Income e WHERE e.user.id = :userId "+
            " AND e.paymentMode = :paymentMode " +  // Check the specific PaymentMode passed
            " AND (cast(:startDate as timestamp) IS NULL OR e.date >= :startDate) "+
            " AND (cast(:endDate as timestamp) IS NULL OR e.date <= :endDate) ")
    BigDecimal calculateTotalIncomeByPaymentMode(@Param("userId") Long userId,
                                                  @Param("paymentMode") PaymentMode paymentMode, // Accept as param

                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate);
}

