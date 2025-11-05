package com.library.app.controller;

import com.library.app.model.OutputModel;
import com.library.app.model.UserModel;
import com.library.app.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserModel userModel){

        return userService.register(userModel);
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> payload) {
        String userName = payload.get("userName");
        String password = payload.get("password");
        log.warn(userName + " " + password);
        return userService.login(userName, password);
    }


}
