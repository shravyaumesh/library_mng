package com.library.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Builder
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="book_model")
public class BookModel {
        @Id
        private String bookId;
        private String title;
        private String author;
        private String genre;
        private String publishedYear;
        private String publisher;
        private String image_L;
        private String image_S;
        private String image_M;
        @OneToMany( cascade = CascadeType.ALL)
        private List<TransactionModel> transaction;

        @Builder.Default
        private Boolean availabilityStatus =Boolean.FALSE;


}
