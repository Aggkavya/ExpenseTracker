package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Receivable;
import com.personal.Expense_Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
}
