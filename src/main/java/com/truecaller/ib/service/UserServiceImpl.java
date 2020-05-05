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

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    CustomUserDetailService userDetailService;


    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) throws BadRequestException{
        Optional<User> user = userRepository.findByUsername(signUpRequest.getUsername());
        if (user.isPresent())
            throw new BadRequestException("User already exists.");

        User newUser = new User();
        newUser.setActive(true);
        newUser.setRoles("ROLE_USER");
        newUser.setUsername(signUpRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setPhone(signUpRequest.getPhone());
        newUser.setName(signUpRequest.getName());
        userRepository.save(newUser);


        final UserDetails userDetails = userDetailService.loadUserByUsername(signUpRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}
