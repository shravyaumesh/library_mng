package com.library.app.repository;

import com.library.app.model.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<TransactionModel , String> {
}
