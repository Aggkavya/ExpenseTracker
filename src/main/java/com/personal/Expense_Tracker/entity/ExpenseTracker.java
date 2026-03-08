package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ExpenseTracker")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float amount;

    private
}
