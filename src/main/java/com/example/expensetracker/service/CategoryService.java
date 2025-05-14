package com.example.expensetracker.service;

import com.example.expensetracker.model.Category;

public interface CategoryService {
    Category findCategoryByName(String name);
    Category findCategoryById(int id);
}
