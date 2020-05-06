package com.truecaller.ib.controller;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.exceptions.NotFoundException;
import com.truecaller.ib.model.SignUpRequest;
import com.truecaller.ib.security.CustomUserDetail;
import com.truecaller.ib.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) throws BadRequestException {
        logger.info("Sign up request received......");
        return userService.createUser(signUpRequest);
    }

    @GetMapping("/search_by_name")
    public ResponseEntity<?> searchByName(@RequestParam String key,
                                          @RequestParam int pageNo,
                                          @RequestParam int pageSize)
            throws BadRequestException, NotFoundException {
        logger.info("Search by name request received.......");
        return userService.searchByName(key, pageNo, pageSize);
    }

    @PostMapping("/spam")
    public ResponseEntity<?> markSpam(@RequestParam String phone,
                                      @AuthenticationPrincipal CustomUserDetail userDetail)
            throws BadRequestException {
        logger.info("Mark spam request received.......");
        return userService.markSpam(phone, userDetail);
    }

    @GetMapping("/search_by_number")
    public ResponseEntity<?> searchByNumber(@RequestParam String key,
                                            @RequestParam int pageNo,
                                            @RequestParam int pageSize)
            throws BadRequestException, NotFoundException {
        logger.info("Search by number request received.......");
        return userService.searchByNumber(key, pageNo, pageSize);
    }

    @GetMapping("/details")
    public ResponseEntity<?> numberDetails(@RequestParam String phone,
                                           @AuthenticationPrincipal CustomUserDetail userDetail)
            throws BadRequestException {
        logger.info("Get number Details request received.......");
        return userService.getNumberDetails(phone, userDetail);
    }

}
