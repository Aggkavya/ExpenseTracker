package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Receivables")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Receivable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String description;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
