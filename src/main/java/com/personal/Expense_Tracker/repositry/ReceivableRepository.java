package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Receivable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
    List<Receivable> findByUserId(Long userId);
    List<Receivable> findByUserIdOrderByDateDesc(Long userId);
}
