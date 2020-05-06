package com.truecaller.ib.service;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.exceptions.NotFoundException;
import com.truecaller.ib.model.SignUpRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<?> createUser(SignUpRequest signUpRequest) throws BadRequestException;

    ResponseEntity<?> searchByName(String key, int pageNo, int pageSize) throws BadRequestException, NotFoundException;
}
