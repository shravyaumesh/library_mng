package com.library.app.controller;

import com.library.app.model.BookModel;

import com.library.app.service.BookService;
import com.library.app.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final BookService bookService;
    private final TransactionService transactionService;
    public BooksController(BookService bookService, TransactionService transactionService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
    }
    @GetMapping("/{start}")
    public ResponseEntity<Object> getBooks(@PathVariable String start){
        return bookService.getAllBooks(start);
    }
    @GetMapping("/book/{id}")
    public ResponseEntity<Object> getBookById(@PathVariable String id){
        return bookService.getBookById(id);
    }
    @PostMapping("/add-books")
    public  ResponseEntity<Object> uploadBook(@RequestBody BookModel book){
        return bookService.postBook(book);
    }
    @GetMapping("/search-book")
    public  ResponseEntity<Object> searchBook(@RequestParam(name="text") String text){

        return bookService.searchBook(text);
            }
    @PostMapping("/{id}/reserve")
    public  ResponseEntity<Object> reserveBooks(@PathVariable String id){
        return  transactionService.reserveBook(id);
    }
    @PostMapping("/{id}/borrow")
    public  ResponseEntity<Object> borrowBooks(@PathVariable String id){
        return  transactionService.borrowBooks(id);
    }
    @PostMapping("/{id}/return")
    public  ResponseEntity<Object> returnBooks(@PathVariable String id){
        return  transactionService.returnBooks(id);
        
    }
    @GetMapping("/reserve")
    public ResponseEntity<Object> getReserved(){
        return transactionService.reservedBook();
    }
    @GetMapping("/borrow")
    public ResponseEntity<Object> getBorrowed(){
        return transactionService.getBorrowed();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable String id){
        return bookService.deleteBook(id);
    }
}
