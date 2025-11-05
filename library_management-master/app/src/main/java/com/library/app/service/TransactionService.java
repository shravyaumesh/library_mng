package com.library.app.service;

import com.library.app.model.BookModel;
import com.library.app.model.OutputModel;
import com.library.app.model.TransactionModel;
import com.library.app.model.UserModel;
import com.library.app.repository.BookRepo;
import com.library.app.repository.TransactionRepo;
import com.library.app.repository.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TransactionService {
    private final BookRepo bookRepo ;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;
    public TransactionService(BookRepo bookRepo , TransactionRepo transactionRepo , UserRepo userRepo){
        this.bookRepo = bookRepo;
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;

    }
    public ResponseEntity<List<TransactionModel>> getTransactions() {
        try{
           List<TransactionModel> transactionModelList = transactionRepo.findAll();
           return new ResponseEntity<>(transactionModelList , HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> reserveBook(String id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BookModel book = bookRepo.findById(id).orElse(null);
        if( book== null ){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userRepo.findByName(userDetails.getUsername());
        if (user == null) {
            log.warn("UserNotFound");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if(book.getAvailabilityStatus()==Boolean.TRUE){
           return new ResponseEntity<>(null, HttpStatus.FOUND);        }
        else {
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setBorrowDate();
            transactionModel.setReturnDate();
            transactionModel.setStatus("Reserved");
            try{
                transactionRepo.save(transactionModel);
                book.getTransaction().add(transactionModel);
                user.getBorrowingHistory().add(transactionModel);
                bookRepo.save(book);
                userRepo.save(user);

            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            return new ResponseEntity<>(transactionModel , HttpStatus.OK);
        }

    }

    public ResponseEntity<Object> borrowBooks(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userRepo.findByName(userDetails.getUsername());
        if (user == null) {
            log.warn("UserNotFound");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        // Retrieve the book using its ID
        BookModel bookModel = bookRepo.findById(id).orElse(null);
        if (bookModel == null) {
            log.warn("Book Not found");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if(bookModel.getAvailabilityStatus() == Boolean.FALSE){
            return new ResponseEntity<>(null, HttpStatus.ALREADY_REPORTED);
        }

        try{
            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setBorrowDate();
            transactionModel.setReturnDate();

            // Set transaction status (borrowed)
            transactionModel.setStatus("Borrow");
            transactionRepo.save(transactionModel);
            // Update the user's borrowing history and the book's transaction list
            user.getBorrowingHistory().add(transactionModel);
            bookModel.getTransaction().add(transactionModel);
            bookModel.setAvailabilityStatus(Boolean.FALSE);

            // Save the transaction model (this will automatically update the user and book models)
            userRepo.save(user);
            bookRepo.save(bookModel);

        } catch (Exception e) {

            return new ResponseEntity<>(null , HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Create a new transaction with a new ID (UUID for uniqueness)


        // Return the book model in the response
        return new ResponseEntity<>(bookModel, HttpStatus.OK);
    }


    public ResponseEntity<Object> returnBooks(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userRepo.findByName(userDetails.getUsername());
        if(user==null){
            log.warn("userNot found ");
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }
        BookModel bookModel= bookRepo.findById(id).orElse(null);
        if(bookModel==null){
            log.warn("Books Not found");
            return new ResponseEntity<>(null , HttpStatus.NOT_FOUND);
        }

        List<TransactionModel> transactions = bookModel.getTransaction();
        List<TransactionModel> userTransactions = user.getBorrowingHistory();


        // Find the transaction that matches both the book and the user
        Optional<TransactionModel> filteredTransaction = transactions.stream()
                .filter(transactionModel ->
                        userTransactions.contains(transactionModel) &&
                                "Borrow".equals(transactionModel.getStatus())
                )
                .findFirst();


        if (filteredTransaction.isPresent()) {
            TransactionModel transaction = filteredTransaction.get();
            transaction.setStatus("Returned");
            bookModel.setAvailabilityStatus(Boolean.TRUE);
            transactionRepo.save(transaction);
            bookRepo.save(bookModel);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }

// If no matching transaction is found
        log.warn("Not found ");
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<List<TransactionModel>> getPendingTransactions() {
        try {
            List<TransactionModel> transactionModelList = transactionRepo.findAll();
            LocalDate currentDate = LocalDate.now();

            transactionModelList = transactionModelList.stream()
                    .filter(transactionModel -> {
                        LocalDate returnDate = transactionModel.getReturnDate()
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return returnDate.isAfter(currentDate.plusDays(1));
                    })
                    .toList();

            return new ResponseEntity<>(transactionModelList, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception for debugging
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> reservedBook() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userRepo.findByName(userDetails.getUsername());

        if (user == null) {
            log.warn("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<TransactionModel> reservedBooks = user.getBorrowingHistory().stream()
                .filter(transactionModel -> "Reserved".equals(transactionModel.getStatus()))
                .toList();

        if (reservedBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No reserved books found");
        }
        return ResponseEntity.ok(reservedBooks);
    }

    public ResponseEntity<Object> getBorrowed() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserModel user = userRepo.findByName(userDetails.getUsername());
        if (user == null) {
            log.warn("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<TransactionModel> borrowedBooks = user.getBorrowingHistory().stream().filter(
                transactionModel -> "Borrow".equals(transactionModel.getStatus())).toList();
        if (borrowedBooks.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No borrowed books found");
        }
        return new ResponseEntity<>(borrowedBooks, HttpStatus.OK);
    }
}
