package com.example.expensetracker.service;

import com.example.expensetracker.model.Client;

public interface ClientService {
    void saveClient(Client client);
    Client findClientById(int id);
}
