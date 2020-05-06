package com.truecaller.ib.service;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.exceptions.NotFoundException;
import com.truecaller.ib.model.SignUpRequest;
import com.truecaller.ib.security.CustomUserDetail;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> createUser(SignUpRequest signUpRequest) throws BadRequestException;

    ResponseEntity<?> searchByName(String key, int pageNo, int pageSize) throws BadRequestException, NotFoundException;

    ResponseEntity<?> markSpam(String phone, CustomUserDetail userDetail) throws BadRequestException;

    ResponseEntity<?> searchByNumber(String key, int pageNo, int pageSize) throws BadRequestException, NotFoundException;

    ResponseEntity<?> getNumberDetails(String phone, CustomUserDetail userDetail) throws BadRequestException;
}
