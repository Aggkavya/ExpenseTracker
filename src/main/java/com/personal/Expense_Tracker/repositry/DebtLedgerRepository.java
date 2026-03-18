package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.DebtLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtLedgerRepository extends JpaRepository<DebtLedger, Long> {
    List<DebtLedger> findByDebtId(Long debtId);
}
