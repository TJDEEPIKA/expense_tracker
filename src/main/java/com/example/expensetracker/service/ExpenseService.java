package com.example.expensetracker.service;

import com.example.expensetracker.DTO.FilterDTO;
import com.example.expensetracker.model.Expense;

import java.util.List;

public interface ExpenseService {
    List<Expense> findFilterResult(FilterDTO filter);
    void save(Expense expense);
    List<Expense> findExpensesByClientIdAndDate(int clientId, String date);
    Expense findExpenseById(int id);
    void update(Expense expense);
    void deleteExpenseById(int id);
}
