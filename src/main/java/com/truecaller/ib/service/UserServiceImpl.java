package com.truecaller.ib.service;

import com.truecaller.ib.entity.User;
import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.model.AuthenticationResponse;
import com.truecaller.ib.model.SignUpRequest;
import com.truecaller.ib.repository.UserRepository;
import com.truecaller.ib.security.CustomUserDetailService;
import com.truecaller.ib.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomUserDetailService userDetailService;


    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) throws BadRequestException {

        if ((Objects.isNull(signUpRequest.getName()) || signUpRequest.getName().isEmpty()) ||
                (Objects.isNull(signUpRequest.getPhone()) || signUpRequest.getPhone().isEmpty()))
            throw new BadRequestException("Name and phone are required.");

        if (Objects.nonNull(signUpRequest.getPhone()) && (signUpRequest.getPhone().length() != 10 ||
                signUpRequest.getPhone().startsWith("0")))
            throw new BadRequestException("Invalid phone number.");


        User newUser = new User();
        newUser.setEmail(Objects.isNull(signUpRequest.getEmail()) ||
                signUpRequest.getEmail().isEmpty() ? null : signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setPhone(signUpRequest.getPhone());
        newUser.setName(signUpRequest.getName());

        try {
            userRepository.save(newUser);
        } catch (Exception exception) {
            throw new BadRequestException("This number is already registered.");
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(signUpRequest.getPhone());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}
