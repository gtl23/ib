package com.truecaller.ib.controller;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.exceptions.NotFoundException;
import com.truecaller.ib.model.SignUpRequest;
import com.truecaller.ib.security.CustomUserDetail;
import com.truecaller.ib.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws BadRequestException {
        return userService.createUser(signUpRequest);
    }

    @GetMapping("/search_by_name")
    public ResponseEntity<?> searchByName(@RequestParam String key,
                                          @RequestParam int pageNo,
                                          @RequestParam int pageSize)
            throws BadRequestException, NotFoundException {
        return userService.searchByName(key, pageNo, pageSize);
    }

    @PostMapping("/spam")
    public ResponseEntity<?> markSpam(@RequestParam String phone,
                                      @AuthenticationPrincipal CustomUserDetail userDetail)
            throws BadRequestException {
        return userService.markSpam(phone, userDetail);
    }

}
