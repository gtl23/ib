package com.truecaller.ib.controller;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.model.SignUpRequest;
import com.truecaller.ib.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws BadRequestException {
        return userService.createUser(signUpRequest);
    }

}
