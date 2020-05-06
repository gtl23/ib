package com.truecaller.ib.service;

import com.truecaller.ib.entity.Spam;
import com.truecaller.ib.entity.User;
import com.truecaller.ib.exceptions.BadRequestException;
import com.truecaller.ib.exceptions.NotFoundException;
import com.truecaller.ib.model.*;
import com.truecaller.ib.repository.ContactsRepository;
import com.truecaller.ib.repository.SpamRepository;
import com.truecaller.ib.repository.UserRepository;
import com.truecaller.ib.security.CustomUserDetail;
import com.truecaller.ib.security.CustomUserDetailService;
import com.truecaller.ib.security.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

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

    @Autowired
    SpamRepository spamRepository;

    @Autowired
    ContactsRepository contactsRepository;


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
        return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<?> searchByName(String key, int pageNo, int pageSize)
            throws BadRequestException, NotFoundException {

        if (key.trim().isEmpty())
            throw new BadRequestException("No search key provided.");

        pageNo = pageNo / pageSize;

        Page<SearchProjection> searchProjectionList =
                userRepository.searchByName(key, PageRequest.of(pageNo, pageSize));

        if (Objects.isNull(searchProjectionList) || searchProjectionList.isEmpty())
            throw new NotFoundException("No records found");

        List<SearchResult> searchResults = new ArrayList<>();
        searchProjectionList.getContent().forEach(searchProjection -> {
            Long spamCount = spamRepository.getSpamCount(searchProjection.getPhone());
            SearchResult searchResult = new SearchResult();
            BeanUtils.copyProperties(searchProjection, searchResult);
            searchResult.setSpamCount(spamCount);
            searchResults.add(searchResult);
        });

        return new ResponseEntity<>(new SearchResponse(searchResults, searchProjectionList.getTotalElements())
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> markSpam(String phone, CustomUserDetail userDetail) throws BadRequestException {

        if (phone.trim().isEmpty())
            throw new BadRequestException("No phone number provided.");

        Spam spam = new Spam();
        spam.setPhone(phone.trim());

        Optional<User> user = userRepository.findByPhone(userDetail.getUsername());
        user.orElseThrow(() -> new BadRequestException("You must be a registered user to report spam"));

        spam.setReportedBy(user.get().getId());

        try {
            spamRepository.save(spam);
        } catch (Exception e) {
            throw new BadRequestException("You've already reported this number as spam.");
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> searchByNumber(String key, int pageNo, int pageSize)
            throws BadRequestException, NotFoundException {

        if (key.trim().isEmpty() || key.length() != 10)
            throw new BadRequestException("Invalid phone number.");

        Optional<User> user = userRepository.findByPhone(key);
        SearchResponse searchResponses = new SearchResponse();
        List<SearchResult> searchResultList = new ArrayList<>();

        if (user.isPresent()) {
            SearchResult searchResult = new SearchResult(user.get().getName(), user.get().getPhone());
            searchResultList.add(searchResult);
            searchResponses.setSearchResults(searchResultList);
            return new ResponseEntity<>(searchResponses, HttpStatus.OK);
        } else {
            pageNo = pageNo / pageSize;
            Page<SearchProjection> searchProjectionList =
                    contactsRepository.findByPhone(key.trim(), PageRequest.of(pageNo, pageSize));

            if (Objects.isNull(searchProjectionList) || searchProjectionList.isEmpty())
                throw new NotFoundException("No records found");

            searchProjectionList.getContent().forEach(searchProjection -> {
                SearchResult searchResult = new SearchResult();
                BeanUtils.copyProperties(searchProjection, searchResult);
                searchResultList.add(searchResult);
            });

            return new ResponseEntity<>(new SearchResponse(searchResultList, searchProjectionList.getTotalElements())
                    , HttpStatus.OK);

        }
    }
}
