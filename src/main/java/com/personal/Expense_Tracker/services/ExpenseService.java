package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.CreateExpenseRequest;
import com.personal.Expense_Tracker.DTO.CreateExpenseResponse;
import com.personal.Expense_Tracker.DTO.GetExpenseResponse;
import com.personal.Expense_Tracker.entity.Category;
import com.personal.Expense_Tracker.entity.Expense;
import com.personal.Expense_Tracker.entity.PaymentMode;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.ExpenseRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    // create new expense
    public CreateExpenseResponse newExpense(CreateExpenseRequest expenseRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);

        // mapping
        CreateExpenseResponse response = new CreateExpenseResponse();
        Expense expense = new Expense();
        expense.setUser(user);

        // Category
        if (expenseRequest.getCategory() != null) {
            expense.setCategory(expenseRequest.getCategory());
            response.setCategory(expenseRequest.getCategory());
        } else {
            expense.setCategory(Category.OTHERS);
            response.setCategory(Category.OTHERS);
        }

        // Payment

        BigDecimal roundCash = expenseRequest.getAmount()
                .setScale(2, RoundingMode.HALF_UP);
        expense.setAmount(roundCash);
        response.setAmount(roundCash);
        if (expenseRequest.getPaymentMode() != null) {

            response.setPaymentMode(expenseRequest.getPaymentMode());
            expense.setPaymentMode(expenseRequest.getPaymentMode());

            // handling balance
            if (expenseRequest.getPaymentMode() == PaymentMode.CASH) {
                if (user.getCashInHand().compareTo(roundCash) < 0) {
                    throw new RuntimeException("Insufficient Cash in Hand balance");
                }
                user.setCashInHand(user.getCashInHand().subtract(roundCash));
            } else {
                if (user.getBankBalance().compareTo(roundCash) < 0) {
                    throw new RuntimeException("Insufficient Bank balance");
                }
                user.setBankBalance(user.getBankBalance().subtract(roundCash));
            }

        } else {
            response.setPaymentMode(PaymentMode.CASH);
            expense.setPaymentMode(PaymentMode.CASH);
            if (user.getCashInHand().compareTo(roundCash) < 0) {
                throw new RuntimeException("Insufficient Cash in Hand balance");
            }
            user.setCashInHand(user.getCashInHand().subtract(roundCash));
        }

        expense.setDate(new java.util.Date());
        response.setDate(expense.getDate());

        response.setBankBalance(user.getBankBalance());
        response.setCashInHand(user.getCashInHand());

        expense.setDescription(expenseRequest.getDescription());
        response.setDescription(expenseRequest.getDescription());

        user.getAllExpenses().add(expense);
        userRepository.save(user);
        expenseRepository.save(expense);

        return response;
    }

    public List<GetExpenseResponse> getAllExpenses() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userRepository.findByUserName(userName);

        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        return expenses.stream().map(expense -> {

            GetExpenseResponse dto = new GetExpenseResponse();

            dto.setAmount(expense.getAmount());
            dto.setCategory(expense.getCategory());
            dto.setDate(expense.getDate());
            dto.setId(expense.getId());
            dto.setDescription(expense.getDescription());
            dto.setPaymentMode(expense.getPaymentMode());

            return dto;

        }).collect(Collectors.toList());
    }

    public List<GetExpenseResponse> getAllExpensesByCategory(Category category){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName);
        List<Expense> expenses = expenseRepository.findByUserIdAndCategoryOrderByDateDesc(user.getId(), category);

        return expenses.stream().map(expense -> {
            GetExpenseResponse dto = new GetExpenseResponse();
            dto.setPaymentMode(expense.getPaymentMode());
            dto.setCategory(expense.getCategory());
            dto.setAmount(expense.getAmount());
            dto.setDate(expense.getDate());
            dto.setDescription(expense.getDescription());
            dto.setId(expense.getId());
            return dto;
        }).collect(Collectors.toList());
    }


    }


