package com.personal.Expense_Tracker.repositry;

import com.personal.Expense_Tracker.entity.Expense;
import com.personal.Expense_Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    Optional<User> findByEmail(String Email);

    // Search users by name OR username (case-insensitive), excludes the current user
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND u.id != :currentUserId")
    List<User> searchUser(@Param("query") String query,
                          @Param("currentUserId") Long currentUserId);
}
