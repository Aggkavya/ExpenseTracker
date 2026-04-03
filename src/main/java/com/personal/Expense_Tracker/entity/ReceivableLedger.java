package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "receivable_ledger")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceivableLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal amount; // Amount collected in this entry

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String description;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receivable_id")
    private Receivable receivable;
}
