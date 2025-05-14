package com.example.expensetracker.service;

import com.example.expensetracker.DTO.FilterDTO;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public List<Expense> findFilterResult(FilterDTO filter) {
        List<Expense> allExpenses = expenseRepository.findAll();

        return allExpenses.stream()
                .filter(expense -> {
                    boolean categoryMatch = filter.getCategory().equalsIgnoreCase("all") || 
                        (expense.getCategory() != null && expense.getCategory().getName().equalsIgnoreCase(filter.getCategory()));
                    boolean monthMatch = filter.getMonth().equalsIgnoreCase("all") || 
                        (expense.getDateTime() != null && expense.getDateTime().substring(5, 7).equals(filter.getMonth()));
                    boolean yearMatch = filter.getYear().equalsIgnoreCase("all") || 
                        (expense.getDateTime() != null && expense.getDateTime().substring(0, 4).equals(filter.getYear()));
                    return categoryMatch && monthMatch && yearMatch;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void save(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public List<Expense> findExpensesByClientIdAndDate(int clientId, String date) {
        return expenseRepository.findByClientIdAndDateTimeStartingWith(clientId, date);
    }

    @Override
    public Expense findExpenseById(int id) {
        return expenseRepository.findById(id).orElse(null);
    }

    @Override
    public void update(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public void deleteExpenseById(int id) {
        expenseRepository.deleteById(id);
    }
}
