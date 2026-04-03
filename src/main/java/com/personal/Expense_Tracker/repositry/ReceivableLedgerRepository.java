package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.ReceivableLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivableLedgerRepository extends JpaRepository<ReceivableLedger, Long> {
    List<ReceivableLedger> findByReceivableId(Long receivableId);
}
