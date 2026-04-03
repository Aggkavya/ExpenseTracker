package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.*;
import com.personal.Expense_Tracker.entity.PaymentMode;
import com.personal.Expense_Tracker.entity.Receivable;
import com.personal.Expense_Tracker.entity.ReceivableLedger;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.ReceivableLedgerRepository;
import com.personal.Expense_Tracker.repositry.ReceivableRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceivableService {

    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private ReceivableLedgerRepository receivableLedgerRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(auth.getName());
    }

    // ── Create ───────────────────────────────────────────────────────────────

    @Transactional
    public CreateReceivableResponse newReceivable(CreateReceivableRequest request) {
        User user = getCurrentUser();

        BigDecimal roundedAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        Boolean isHistorical = request.getIsHistorical() != null ? request.getIsHistorical() : false;
        PaymentMode mode = request.getPaymentMode() != null ? request.getPaymentMode() : PaymentMode.CASH;

        Receivable receivable = new Receivable();
        receivable.setUser(user);
        receivable.setAmount(roundedAmount);
        receivable.setRemainingAmount(roundedAmount);
        receivable.setPaymentMode(mode);
        receivable.setIsHistorical(isHistorical);
        receivable.setDescription(request.getDescription());
        receivable.setDate(new java.util.Date());

        // If NON-historical, the other person already paid us — add to our balance
        if (!isHistorical) {
            if (mode == PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().add(roundedAmount));
            } else {
                user.setBankBalance(user.getBankBalance().add(roundedAmount));
            }
        }

        userRepository.save(user);
        receivableRepository.save(receivable);

        CreateReceivableResponse response = new CreateReceivableResponse();
        response.setAmount(roundedAmount);
        response.setPaymentMode(mode);
        response.setIsHistorical(isHistorical);
        response.setDescription(request.getDescription());
        response.setDate(receivable.getDate());
        response.setCashInHand(user.getCashInHand());
        response.setBankBalance(user.getBankBalance());
        return response;
    }

    // ── Read All ─────────────────────────────────────────────────────────────

    public List<GetReceivableResponse> getAllReceivables() {
        User user = getCurrentUser();
        List<Receivable> receivables = receivableRepository.findByUserIdOrderByDateDesc(user.getId());

        return receivables.stream().map(r -> {
            GetReceivableResponse dto = new GetReceivableResponse();
            dto.setId(r.getId());
            dto.setAmount(r.getAmount());
            dto.setRemainingAmount(r.getRemainingAmount());
            dto.setPaymentMode(r.getPaymentMode());
            dto.setDescription(r.getDescription());
            dto.setDate(r.getDate());
            dto.setIsHistorical(r.getIsHistorical());

            List<ReceivableLedgerResponse> ledgerDtos = r.getLedgers().stream().map(l -> {
                ReceivableLedgerResponse lDto = new ReceivableLedgerResponse();
                lDto.setId(l.getId());
                lDto.setAmount(l.getAmount());
                lDto.setPaymentMode(l.getPaymentMode());
                lDto.setDescription(l.getDescription());
                lDto.setDate(l.getDate());
                return lDto;
            }).collect(Collectors.toList());

            dto.setLedgers(ledgerDtos);
            return dto;
        }).collect(Collectors.toList());
    }

    // ── Collect (partial or full) ─────────────────────────────────────────────

    @Transactional
    public CollectReceivableResponse collectReceivable(CollectReceivableRequest request) {
        User user = getCurrentUser();

        Receivable receivable = receivableRepository.findById(request.getReceivableId())
                .orElseThrow(() -> new RuntimeException("Receivable not found"));

        if (!receivable.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Receivable doesn't belong to the user");
        }

        BigDecimal collectAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);

        if (receivable.getRemainingAmount().compareTo(collectAmount) < 0) {
            throw new RuntimeException("Collection amount exceeds remaining receivable");
        }

        PaymentMode payMode = request.getPaymentMode() != null ? request.getPaymentMode() : PaymentMode.CASH;

        // Add collected amount to user's balance
        if (payMode == PaymentMode.CASH) {
            user.setCashInHand(user.getCashInHand().add(collectAmount));
        } else {
            user.setBankBalance(user.getBankBalance().add(collectAmount));
        }

        receivable.setRemainingAmount(receivable.getRemainingAmount().subtract(collectAmount));

        ReceivableLedger ledger = new ReceivableLedger();
        ledger.setReceivable(receivable);
        ledger.setAmount(collectAmount);
        ledger.setPaymentMode(payMode);
        ledger.setDescription(request.getDescription());
        ledger.setDate(new java.util.Date());

        receivableLedgerRepository.save(ledger);
        receivableRepository.save(receivable);
        userRepository.save(user);

        CollectReceivableResponse response = new CollectReceivableResponse();
        response.setLedgerId(ledger.getId());
        response.setReceivableId(receivable.getId());
        response.setCollectedAmount(collectAmount);
        response.setRemainingAmount(receivable.getRemainingAmount());
        response.setPaymentMode(payMode);
        response.setCashInHand(user.getCashInHand());
        response.setBankBalance(user.getBankBalance());
        response.setCollectionDate(ledger.getDate());
        response.setDescription(ledger.getDescription());
        return response;
    }

    // ── History ───────────────────────────────────────────────────────────────

    public List<GetReceivableHistoryResponse> getReceivableHistory(Long receivableId) {
        User user = getCurrentUser();

        Receivable receivable = receivableRepository.findById(receivableId)
                .orElseThrow(() -> new RuntimeException("Receivable not found"));

        if (!receivable.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Receivable doesn't belong to the user");
        }

        List<ReceivableLedger> ledgers = receivableLedgerRepository.findByReceivableId(receivableId);

        return ledgers.stream().map(l -> {
            GetReceivableHistoryResponse dto = new GetReceivableHistoryResponse();
            dto.setId(l.getId());
            dto.setAmountCollected(l.getAmount());
            dto.setPaymentMode(l.getPaymentMode());
            dto.setDescription(l.getDescription());
            dto.setDate(l.getDate());
            return dto;
        }).collect(Collectors.toList());
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteReceivable(Long id) {
        User user = getCurrentUser();

        Receivable receivable = receivableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receivable not found"));

        if (!receivable.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Receivable doesn't belong to the user");
        }

        // Reverse original non-historical add (subtract original amount)
        if (receivable.getIsHistorical() == null || !receivable.getIsHistorical()) {
            if (receivable.getPaymentMode() == PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().subtract(receivable.getAmount()));
            } else {
                user.setBankBalance(user.getBankBalance().subtract(receivable.getAmount()));
            }
        }

        // Reverse all collections recorded in ledgers (subtract them back)
        for (ReceivableLedger ledger : receivable.getLedgers()) {
            if (ledger.getPaymentMode() == PaymentMode.CASH) {
                user.setCashInHand(user.getCashInHand().subtract(ledger.getAmount()));
            } else {
                user.setBankBalance(user.getBankBalance().subtract(ledger.getAmount()));
            }
        }

        receivableRepository.deleteById(id);
        userRepository.save(user);
    }
}
