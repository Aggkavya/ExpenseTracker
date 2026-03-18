package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
@Entity
@Table(name = "Debts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Debt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount; // Original debt amount

    @Column(precision = 19, scale = 2)
    private BigDecimal remainingAmount; // Amount remaining to be paid

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String description;
    private Date date;

    private Boolean isHistorical;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DebtLedger> ledgers = new ArrayList<>();
}
