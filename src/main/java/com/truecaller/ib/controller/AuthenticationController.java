package com.truecaller.ib.controller;

import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.model.AuthenticationRequest;
import com.truecaller.ib.model.AuthenticationResponse;
import com.truecaller.ib.security.CustomUserDetailService;
import com.truecaller.ib.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailService userDetailService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest request) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(),
                            request.getPassword())
            );
        }catch (BadCredentialsException e){
            throw new BadRequestException("Invalid credentials or account doesn't exists.");
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(request.getPhone());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }

}
