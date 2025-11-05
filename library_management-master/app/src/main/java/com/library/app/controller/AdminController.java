package com.library.app.controller;

import com.library.app.model.TransactionModel;
import com.library.app.model.UserModel;
import com.library.app.service.TransactionService;
import com.library.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    private final TransactionService transactionService;
    public AdminController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }
    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> users(){
        return userService.users();
    }
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionModel>> transactions(){
        return transactionService.getTransactions();
    }
    @GetMapping("/pendingTransactions")
    public ResponseEntity<List<TransactionModel>> pendingTransactions(){
        return transactionService.getPendingTransactions();
    }
}
