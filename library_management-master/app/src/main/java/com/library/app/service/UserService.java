package com.library.app.service;

import com.library.app.model.OutputModel;
import com.library.app.model.Role;
import com.library.app.model.UserModel;
import com.library.app.repository.UserRepo;

import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder ;
    UserService(UserRepo userRepo , JWTService jwtService){
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        encoder = new BCryptPasswordEncoder(5);
    }


    public ResponseEntity<Object> register(UserModel userModel) {
        UserModel user ;
        userModel.setPassword(encoder.encode(
                userModel.getPassword()
        ));
        user = userRepo.findByName(userModel.getName());
        try {
            if (user == null ) {
                userModel.setRole(Role.USER);
                userRepo.save(userModel);
                String token = jwtService.getToken(userModel);
                OutputModel output = new OutputModel(token);
                return new ResponseEntity<>(output.getObject(), HttpStatus.OK);
            }
            else {
                OutputModel output = new OutputModel("Already found");
                return new ResponseEntity<>(output.getObject(), HttpStatus.ALREADY_REPORTED);
            }}

        catch (Exception e) {
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<UserModel>> users() {
        try {
            List<UserModel> users = userRepo.findAll();
            return new ResponseEntity<>(users,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.BAD_GATEWAY);
        }

    }


    public ResponseEntity<Object> login(String userName, String password) {
        UserModel user ;
        try{
            user = userRepo.findUser(userName);
            if(user != null && encoder.matches(password, user.getPassword()) ){
                String token = jwtService.getToken(user);
                OutputModel output = new OutputModel(token);
                return new ResponseEntity<>(output.getObject(), HttpStatus.OK);
            }
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject("Not Found"), HttpStatus.NOT_FOUND);
        }
        catch (Exception e ){
            OutputModel output = new OutputModel();
            return new ResponseEntity<>(output.getObject(e.getMessage()),HttpStatus.BAD_GATEWAY);
        }

    }
}
