package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.CreateDebtRequest;
import com.personal.Expense_Tracker.DTO.CreateDebtResponse;
import com.personal.Expense_Tracker.entity.Debt;
import com.personal.Expense_Tracker.entity.PaymentMode;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.DebtLedgerRepository;
import com.personal.Expense_Tracker.repositry.DebtRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DebtService {
    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private DebtLedgerRepository debtLedgerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CreateDebtResponse newDebt(CreateDebtRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);

        CreateDebtResponse response = new CreateDebtResponse();
        Debt debt = new Debt();
        debt.setUser(user);

        BigDecimal roundedAmount = request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP);
        debt.setAmount(roundedAmount);
        debt.setRemainingAmount(roundedAmount);
        response.setAmount(roundedAmount);

        Boolean isHistorical = request.getIsHistorical() != null ? request.getIsHistorical() : false;
        debt.setIsHistorical(isHistorical);
        response.setIsHistorical(isHistorical);

        PaymentMode mode = request.getPaymentMode() != null ? request.getPaymentMode() : PaymentMode.CASH;
        debt.setPaymentMode(mode);
        response.setPaymentMode(mode);

        if (!isHistorical) {
            if (mode == PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().add(roundedAmount));
            } else {
                user.setBankBalance(user.getBankBalance().add(roundedAmount));
            }
        }

        debt.setDate(new java.util.Date());
        response.setDate(debt.getDate());

        debt.setDescription(request.getDescription());
        response.setDescription(request.getDescription());

        userRepository.save(user);
        debtRepository.save(debt);

        response.setCashInHand(user.getCashInHand());
        response.setBankBalance(user.getBankBalance());

        return response;
    }

    public java.util.List<com.personal.Expense_Tracker.DTO.GetDebtResponse> getAllDebts() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        com.personal.Expense_Tracker.entity.User user = userRepository.findByUserName(userName);

        java.util.List<com.personal.Expense_Tracker.entity.Debt> debts = debtRepository.findByUserIdOrderByDateDesc(user.getId());

        return debts.stream().map(debt -> {
            com.personal.Expense_Tracker.DTO.GetDebtResponse dto = new com.personal.Expense_Tracker.DTO.GetDebtResponse();
            dto.setId(debt.getId());
            dto.setAmount(debt.getAmount());
            dto.setRemainingAmount(debt.getRemainingAmount());
            dto.setPaymentMode(debt.getPaymentMode());
            dto.setDescription(debt.getDescription());
            dto.setDate(debt.getDate());
            dto.setIsHistorical(debt.getIsHistorical());

            java.util.List<com.personal.Expense_Tracker.DTO.DebtLedgerResponse> ledgerResponses = debt.getLedgers().stream().map(ledger -> {
                com.personal.Expense_Tracker.DTO.DebtLedgerResponse lDto = new com.personal.Expense_Tracker.DTO.DebtLedgerResponse();
                lDto.setId(ledger.getId());
                lDto.setAmount(ledger.getAmount());
                lDto.setPaymentMode(ledger.getPaymentMode());
                lDto.setDescription(ledger.getDescription());
                lDto.setDate(ledger.getDate());
                return lDto;
            }).collect(java.util.stream.Collectors.toList());

            dto.setLedgers(ledgerResponses);
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @org.springframework.transaction.annotation.Transactional
    public com.personal.Expense_Tracker.DTO.PayDebtResponse payDebt(com.personal.Expense_Tracker.DTO.PayDebtRequest request) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        com.personal.Expense_Tracker.entity.User user = userRepository.findByUserName(userName);

        com.personal.Expense_Tracker.entity.Debt debt = debtRepository.findById(request.getDebtId())
                .orElseThrow(() -> new RuntimeException("Debt not found"));

        if (!debt.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Debt doesn't belong to the user");
        }

        java.math.BigDecimal payAmount = request.getAmount().setScale(2, java.math.RoundingMode.HALF_UP);

        if (debt.getRemainingAmount().compareTo(payAmount) < 0) {
            throw new RuntimeException("Payment amount exceeds remaining debt");
        }

        com.personal.Expense_Tracker.entity.PaymentMode payMode = request.getPaymentMode() != null ? request.getPaymentMode() : com.personal.Expense_Tracker.entity.PaymentMode.CASH;

        if (payMode == com.personal.Expense_Tracker.entity.PaymentMode.CASH) {
            if (user.getCashInHand().compareTo(payAmount) < 0) {
                throw new RuntimeException("Insufficient Cash in Hand balance");
            }
            user.setCashInHand(user.getCashInHand().subtract(payAmount));
        } else {
            if (user.getBankBalance().compareTo(payAmount) < 0) {
                throw new RuntimeException("Insufficient Bank balance");
            }
            user.setBankBalance(user.getBankBalance().subtract(payAmount));
        }

        debt.setRemainingAmount(debt.getRemainingAmount().subtract(payAmount));

        com.personal.Expense_Tracker.entity.DebtLedger ledger = new com.personal.Expense_Tracker.entity.DebtLedger();
        ledger.setDebt(debt);
        ledger.setAmount(payAmount);
        ledger.setPaymentMode(payMode);
        ledger.setDescription(request.getDescription());
        ledger.setDate(new java.util.Date());

        debtLedgerRepository.save(ledger);
        debtRepository.save(debt);
        userRepository.save(user);

        com.personal.Expense_Tracker.DTO.PayDebtResponse response = new com.personal.Expense_Tracker.DTO.PayDebtResponse();
        response.setLedgerId(ledger.getId());
        response.setDebtId(debt.getId());
        response.setPaidAmount(payAmount);
        response.setRemainingAmount(debt.getRemainingAmount());
        response.setPaymentMode(payMode);
        response.setCashInHand(user.getCashInHand());
        response.setBankBalance(user.getBankBalance());
        response.setPaymentDate(ledger.getDate());
        response.setDescription(ledger.getDescription());

        return response;
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteDebt(Long id) {
        String userName = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.personal.Expense_Tracker.entity.User user = userRepository.findByUserName(userName);
        Long userId = user.getId();

        com.personal.Expense_Tracker.entity.Debt debt = debtRepository.findById(id).orElseThrow(() -> new RuntimeException("Debt not found"));
        if (!debt.getUser().getId().equals(userId)) {
            throw new RuntimeException("Debt doesn't belong to the user");
        }

        if (debt.getIsHistorical() == null || !debt.getIsHistorical()) {
            if (debt.getPaymentMode() == com.personal.Expense_Tracker.entity.PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().subtract(debt.getAmount()));
            } else {
                user.setBankBalance(user.getBankBalance().subtract(debt.getAmount()));
            }
        }

        for (com.personal.Expense_Tracker.entity.DebtLedger ledger : debt.getLedgers()) {
            if (ledger.getPaymentMode() == com.personal.Expense_Tracker.entity.PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().add(ledger.getAmount()));
            } else {
                user.setBankBalance(user.getBankBalance().add(ledger.getAmount()));
            }
        }

        debtRepository.deleteById(id);
        userRepository.save(user);
    }
}
