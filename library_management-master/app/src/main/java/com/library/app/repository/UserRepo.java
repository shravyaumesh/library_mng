package com.library.app.repository;

import com.library.app.model.BookModel;
import com.library.app.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<UserModel,String> {
    UserModel findByName(String name);

    @Query("SELECT u FROM UserModel u WHERE u.name = :userName OR u.email = :userName")
    UserModel findUser(String userName);
    }
