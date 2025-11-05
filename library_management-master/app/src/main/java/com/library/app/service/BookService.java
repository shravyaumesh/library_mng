package com.library.app.service;

import com.library.app.model.BookModel;
import com.library.app.model.OutputModel;

import com.library.app.repository.BookRepo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;



@Service
public class BookService {

    private final BookRepo bookRepo;



    BookService(BookRepo bookRepo ){
        this.bookRepo = bookRepo;


    }
    public ResponseEntity<Object> getAllBooks(String start) {
        try {
            // Parse the start value
            int startInt = Integer.parseInt(start);

            // Create a Pageable object with the desired page and size
            Pageable pageable = PageRequest.of(startInt / 10, 10); // Divide start by 10 to get the page index

            // Fetch the books using pagination
            Page<BookModel> bookPage = bookRepo.findAll(pageable);

            // Return the content of the page
            OutputModel output = new OutputModel(bookPage.getContent());
            return new ResponseEntity<>(output.getObject(), HttpStatus.OK);
        } catch (Exception e) {
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }


    public ResponseEntity<Object> getBookById(String id) {
        BookModel books;
        try{
            System.out.println(id);
            books = bookRepo.findById(id).orElse(null);
            System.out.println(books);
            return new ResponseEntity<>(books, HttpStatus.OK);
        }catch (Exception e){
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.valueOf(403));
        }
    }

    public ResponseEntity<Object> postBook(BookModel book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OutputModel output = new OutputModel();
        try {
            BookModel oldBook = bookRepo.findById(book.getBookId()).orElse(null);
            if(oldBook!=null){

                return new ResponseEntity<>(output.getObject("Already present") , HttpStatus.ALREADY_REPORTED);
            }else {
                BookModel newBook = bookRepo.save(book);

                return new ResponseEntity<>(newBook, HttpStatus.OK);
            }
        }
        catch (Exception e ){
            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Object> deleteBook(String id) {
        OutputModel output = new OutputModel();
        try{
            BookModel book = bookRepo.findById(id).orElse(null);
            if(book==null){
                return new ResponseEntity<>(output.getObject("Not found") , HttpStatus.NOT_FOUND);
            }
            bookRepo.deleteById(id);
            return new ResponseEntity<>(book , HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> searchBook(String text) {
        try {

            int startInt =20;
            Pageable pageable = PageRequest.of(startInt / 10, 10);
        Page<BookModel> bookPage = bookRepo.findAllByTitleContaining(text, pageable);
            // Return the content of the page
            OutputModel output = new OutputModel(bookPage.getContent());
            return new ResponseEntity<>(output.getObject(), HttpStatus.OK);
        } catch (Exception e) {
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.FORBIDDEN);
        }
    }
}
