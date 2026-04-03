package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private BigDecimal amount; // Original receivable amount

    @Column(precision = 19, scale = 2)
    private BigDecimal remainingAmount; // Amount still to be collected

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private String description;
    private Date date;

    // true = receivable existed before app — do NOT add to current balance
    private Boolean isHistorical;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "receivable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceivableLedger> ledgers = new ArrayList<>();
}
