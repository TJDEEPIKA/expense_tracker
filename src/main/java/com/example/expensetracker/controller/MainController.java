package com.example.expensetracker.controller;

import com.example.expensetracker.DTO.ExpenseDTO;
import com.example.expensetracker.DTO.FilterDTO;
import com.example.expensetracker.model.Client;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.CategoryRepository;
import com.example.expensetracker.repository.ClientRepository;
import com.example.expensetracker.service.ExpenseService;
import com.example.expensetracker.model.Category;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class MainController {
    ExpenseService expenseService;
    CategoryRepository categoryRepository;
    ClientRepository clientRepository;

    @Autowired
    public MainController(ExpenseService expenseService, CategoryRepository categoryRepository, ClientRepository clientRepository) {
        this.expenseService = expenseService;
        this.categoryRepository = categoryRepository;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/")
    public String landingPage(HttpSession session, Model model){
        Client client = (Client) session.getAttribute("client");
        if (client != null) {
            model.addAttribute("sessionClient", client);
        }
        return "landing-page";
    }

    @GetMapping("/showAdd")
    public String addExpense(Model model){
        model.addAttribute("expense", new ExpenseDTO());
        return "add-expense";
    }

    @PostMapping("/submitAdd")
    public String submitAdd(@ModelAttribute("expense") ExpenseDTO expenseDTO, HttpSession session){
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        expenseDTO.setClientId(client.getId());
        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());
        // Fetch managed Client entity from DB to avoid detached entity error
        Client managedClient = clientRepository.findById(client.getId()).orElse(null);
        if (managedClient == null) {
            return "redirect:/showLoginPage"; // or handle error
        }
        expense.setClient(managedClient);
        // Map category string to Category entity
        Category category = categoryRepository.findByName(expenseDTO.getCategory());
        if (category == null) {
            // Handle category not found, set to null or default category
            // For now, set null
            expense.setCategory(null);
        } else {
            expense.setCategory(category);
        }
        try {
            expenseService.save(expense);
        } catch (Exception e) {
            e.printStackTrace();
            return "error-page"; // Create a generic error page or handle accordingly
        }
        return "redirect:/list";
    }

    @GetMapping("/list")
    public String list(Model model, HttpSession session){
        Client client = (Client) session.getAttribute("client");
        if (client == null) {
            return "redirect:/showLoginPage";
        }
        int clientId = client.getId();

        String todayDate = java.time.LocalDate.now().toString();

        List<Expense> expenseList = expenseService.findExpensesByClientIdAndDate(clientId, todayDate);

        for (Expense expense : expenseList){
            if (expense.getCategory() != null) {
                expense.setCategoryName(expense.getCategory().getName());
            } else {
                expense.setCategoryName("Unknown");
            }
            expense.setDate(LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate().toString());
            expense.setTime(LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime().toString());
        }
        model.addAttribute("expenseList", expenseList);
        model.addAttribute("filter", new FilterDTO());
        return "list-page";
    }

    @GetMapping("/showUpdate")
    public String showUpdate(@RequestParam("expId") int id, Model model){
        Expense expense = expenseService.findExpenseById(id);
        if (expense == null) {
            // Redirect to list page if expense not found
            return "redirect:/list";
        }
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setAmount(expense.getAmount());
        if (expense.getCategory() != null) {
            expenseDTO.setCategory(expense.getCategory().getName());
        } else {
            expenseDTO.setCategory("");
        }
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setDateTime(expense.getDateTime());

        model.addAttribute("expense", expenseDTO);
        model.addAttribute("expenseId", id);
        return "update-page";
    }

    @PostMapping("/submitUpdate")
    public String update(@RequestParam("expId") int id, @ModelAttribute("expense") ExpenseDTO expenseDTO, HttpSession session){
        Client client = (Client) session.getAttribute("client");
        expenseDTO.setExpenseId(id);
        expenseDTO.setClientId(client.getId());
        Expense expense = new Expense();
        expense.setId(id);
        expense.setAmount(expenseDTO.getAmount());
        expense.setDateTime(expenseDTO.getDateTime());
        expense.setDescription(expenseDTO.getDescription());
        // Set client
        Client managedClient = clientRepository.findById(client.getId()).orElse(null);
        if (managedClient == null) {
            return "redirect:/showLoginPage"; // or handle error
        }
        expense.setClient(managedClient);
        // Set category
        Category category = categoryRepository.findByName(expenseDTO.getCategory());
        if (category == null) {
            expense.setCategory(null);
        } else {
            expense.setCategory(category);
        }
        expenseService.update(expense);
        return "redirect:/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("expId") int id){
        expenseService.deleteExpenseById(id);
        return "redirect:/list";
    }


    @PostMapping("/process-filter")
    public String processFilter(@ModelAttribute("filter") FilterDTO filter, Model model, HttpSession session){
        List<Expense> expenseList = expenseService.findFilterResult(filter);

        for (Expense expense : expenseList){
            if (expense.getCategory() != null) {
                var catOpt = categoryRepository.findById(expense.getCategory().getId());
                String catName = catOpt.isPresent() ? catOpt.get().getName() : null;
                if (catName == null || catName.isEmpty()) {
                    expense.setCategoryName("Unknown");
                } else {
                    expense.setCategoryName(catName);
                }
            } else {
                expense.setCategoryName("Unknown");
            }
            try {
                expense.setDate(LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate().toString());
                expense.setTime(LocalDateTime.parse(expense.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime().toString());
            } catch (Exception e) {
                expense.setDate("Unknown");
                expense.setTime("Unknown");
            }
        }
        // Calculate total amount
        double totalAmount = expenseList.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        model.addAttribute("expenseList", expenseList);
        model.addAttribute("totalAmount", totalAmount);

        // Removed originalExpenseList fetching and adding to model

        return "filter-result";
    }


}
