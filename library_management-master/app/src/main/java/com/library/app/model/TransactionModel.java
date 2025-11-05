package com.library.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Entity
@Table(name = "transaction_model")
public class TransactionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer transactionId;

    @Setter(AccessLevel.NONE)
    private Date borrowDate;

    @Setter(AccessLevel.NONE)
    private Date returnDate;

    private String status;


    public void setBorrowDate() {
        if (this.borrowDate == null) {
            LocalDate currentDate = LocalDate.now();
            this.borrowDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }


    public void setReturnDate() {
        if (this.returnDate == null) {
            LocalDate currentDate = LocalDate.now();
            this.returnDate = Date.from(currentDate.plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }


}
