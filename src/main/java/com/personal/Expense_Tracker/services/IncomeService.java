package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.*;
import com.personal.Expense_Tracker.entity.*;
import com.personal.Expense_Tracker.exceptions.InsufficientBalanceException;
import com.personal.Expense_Tracker.repositry.IncomeRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CreateIncomeResponse newIncome(CreateIncomeRequest incomeRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);

        // mapping
        CreateIncomeResponse response = new CreateIncomeResponse();
        Income income = new Income();
        income.setUser(user);

        // Category


        // Payment

        BigDecimal roundCash = incomeRequest.getAmount()
                .setScale(2, RoundingMode.HALF_UP);
        income.setAmount(roundCash);
        response.setAmount(roundCash);
        if (incomeRequest.getPaymentMode() != null) {

            response.setPaymentMode(incomeRequest.getPaymentMode());
            income.setPaymentMode(incomeRequest.getPaymentMode());

            // handling balance
            if (incomeRequest.getPaymentMode() == PaymentMode.CASH) {

                user.setCashInHand(user.getCashInHand().add(roundCash));
            } else {

                user.setBankBalance(user.getBankBalance().add(roundCash));
            }

        } else {
            response.setPaymentMode(PaymentMode.CASH);
            income.setPaymentMode(PaymentMode.CASH);

            user.setCashInHand(user.getCashInHand().add(roundCash));
        }

        income.setDate(new java.util.Date());
        response.setDate(income.getDate());

        response.setBankBalance(user.getBankBalance());
        response.setCashInHand(user.getCashInHand());

        income.setDescription(incomeRequest.getDescription());
        response.setDescription(incomeRequest.getDescription());

        userRepository.save(user);
        incomeRepository.save(income);

        return response;
    }

    public List<GetIncomeResponse> getAllIncome() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userRepository.findByUserName(userName);

        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(user.getId());

        return incomes.stream().map(expense -> {

            GetIncomeResponse dto = new GetIncomeResponse();

            dto.setAmount(expense.getAmount());

            dto.setDate(expense.getDate());
            dto.setId(expense.getId());
            dto.setDescription(expense.getDescription());
            dto.setPaymentMode(expense.getPaymentMode());

            return dto;

        }).collect(Collectors.toList());
    }

    public GetTotalAmountIncome getTotalIncomeFilter( Date startDate, Date endDate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);
        BigDecimal total = incomeRepository.calculateTotalIncome(user.getId(), startDate, endDate);
        BigDecimal cash = incomeRepository.calculateTotalIncomeByPaymentMode(user.getId(), PaymentMode.CASH,
                startDate, endDate);
        BigDecimal online = incomeRepository.calculateTotalIncomeByPaymentMode(user.getId(), PaymentMode.ONLINE,
                 startDate, endDate);
        GetTotalAmountIncome income = new GetTotalAmountIncome();
        income.setTotalIncome(total = total == null ? BigDecimal.ZERO : total);
        income.setTotalCashIncome(cash = cash == null ? BigDecimal.ZERO : cash);
        income.setTotalOnlineIncome(online = online == null ? BigDecimal.ZERO : online);
        return income;
    }

    @Transactional
    public void DeleteIncomeById(Long id) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(userName);
        Long userId = user.getId();

        try {
            Income income = incomeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("no expense with id"));
            if (income.getUser().getId().equals(userId)) {
                if (income.getPaymentMode().equals(PaymentMode.CASH)) {
                    user.setCashInHand(user.getCashInHand().subtract(income.getAmount()));
                } else {
                    user.setBankBalance(user.getBankBalance().subtract(income.getAmount()));
                }
                incomeRepository.deleteById(id);
                userRepository.save(user);
            } else {
                throw new RuntimeException("expense doesn't belong to the user");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }

}
