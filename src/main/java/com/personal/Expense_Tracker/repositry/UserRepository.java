package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
