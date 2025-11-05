package com.library.app.repository;

import com.library.app.model.BookModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepo extends JpaRepository<BookModel,String> {
    @Query("SELECT b FROM BookModel b WHERE b.title LIKE %?1% OR b.author LIKE %?1%")
    Page<BookModel> findAllByTitleContaining(String text, Pageable pageable);
}
