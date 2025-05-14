package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByClientId(int id);

    List<Expense> findByClientIdAndDateTimeStartingWith(int clientId, String datePrefix);
}
