package com.truecaller.ib.service;

import com.truecaller.ib.entity.Contacts;
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
import com.truecaller.ib.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public ResponseEntity<?> createUser(SignUpRequest signUpRequest) throws BadRequestException {
        logger.info("Inside UserServiceImpl.createUser().......");

        if ((Objects.isNull(signUpRequest.getName()) || signUpRequest.getName().isEmpty()) ||
                (Objects.isNull(signUpRequest.getPhone()) || signUpRequest.getPhone().isEmpty())) {
            logger.info(ResponseMessages.NAME_AND_PHONE_REQUIRED);
            throw new BadRequestException(ResponseMessages.NAME_AND_PHONE_REQUIRED);
        }

        if (Objects.nonNull(signUpRequest.getPhone()) && (signUpRequest.getPhone().length() != 10 ||
                signUpRequest.getPhone().startsWith("0"))){
            logger.info(ResponseMessages.INVALID_PHONE_NUMBER);
            throw new BadRequestException(ResponseMessages.INVALID_PHONE_NUMBER);
        }


        User newUser = new User();
        newUser.setEmail(Objects.isNull(signUpRequest.getEmail()) ||
                signUpRequest.getEmail().isEmpty() ? null : signUpRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        newUser.setPhone(signUpRequest.getPhone());
        newUser.setName(signUpRequest.getName());

        /*
        Since we've unique key constraint for phone number in the database,
        we don't need to bother for checking the existence of a record
        before inserting, simply insert the record. If this throws any exception,
        it means the record is already present.
         */
        try {
            userRepository.save(newUser);
        } catch (Exception exception) {
            logger.info(ResponseMessages.ALREADY_REGISTERED);
            throw new BadRequestException(ResponseMessages.ALREADY_REGISTERED);
        }

        /*
        Returning the jwt after successful creation of record
         */
        final UserDetails userDetails = userDetailService.loadUserByUsername(signUpRequest.getPhone());
        final String jwt = jwtUtil.generateToken(userDetails);
        logger.info(ResponseMessages.SIGN_UP_SUCCESSFUL);
        return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<?> searchByName(String key, int pageNo, int pageSize)
            throws BadRequestException, NotFoundException {
        logger.info("Inside UserServiceImpl.searchByName().......");

        if (pageNo < 0 || pageSize < 1)
            throw new BadRequestException(ResponseMessages.PAGINATION_MESSAGE);

        if (key.trim().isEmpty()){
            logger.info(ResponseMessages.NO_SEARCH_KEY);
            throw new BadRequestException(ResponseMessages.NO_SEARCH_KEY);
        }

        pageNo = pageNo / pageSize;

        Page<SearchProjection> searchProjectionList =
                userRepository.searchByName(key, PageRequest.of(pageNo, pageSize));

        if (Objects.isNull(searchProjectionList) || searchProjectionList.isEmpty()){
            logger.info(ResponseMessages.NO_RECORDS_FOUND);
            throw new NotFoundException(ResponseMessages.NO_RECORDS_FOUND);
        }

        List<SearchResult> searchResults = new ArrayList<>();
        searchProjectionList.getContent().forEach(searchProjection -> {
            Long spamCount = spamRepository.getSpamCount(searchProjection.getPhone());
            SearchResult searchResult = new SearchResult();
            BeanUtils.copyProperties(searchProjection, searchResult);
            searchResult.setSpamCount(spamCount);
            searchResults.add(searchResult);
        });

        logger.info(ResponseMessages.SEARCH_BY_NAME_RESPONSE);

        return new ResponseEntity<>(new SearchResponse(searchResults, searchProjectionList.getTotalElements())
                , HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> markSpam(String phone, CustomUserDetail userDetail) throws BadRequestException {

        logger.info("Inside UserServiceImpl.markSpam().......");

        if (phone.trim().isEmpty()){
            logger.info(ResponseMessages.NO_PHONE_PROVIDED);
            throw new BadRequestException(ResponseMessages.NO_PHONE_PROVIDED);
        }

        Spam spam = new Spam();
        spam.setPhone(phone.trim());

        /*
        If a malicious user somehow manages the bypass the security,
        we'll verify them here in our records
         */
        Optional<User> user = userRepository.findByPhone(userDetail.getUsername());
        User userData = user.orElseThrow(() -> new BadRequestException(ResponseMessages.NOT_REGISTERED));

        spam.setReportedBy(userData.getId());

        /*
        To prevent the user from marking a number as spam multiple times,
        we've (phone_number, reported_by) unique key constraint in the database
         */
        try {
            spamRepository.save(spam);
        } catch (Exception e) {
            logger.info(ResponseMessages.ALREADY_REPORTED_SPAM);
            throw new BadRequestException(ResponseMessages.ALREADY_REPORTED_SPAM);
        }

        logger.info(ResponseMessages.MARKED_SPAM);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> searchByNumber(String key, int pageNo, int pageSize)
            throws BadRequestException, NotFoundException {

        logger.info("Inside UserServiceImpl.searchByNumber().......");

        if (pageNo < 0 || pageSize < 1)
            throw new BadRequestException(ResponseMessages.PAGINATION_MESSAGE);

        if (key.trim().isEmpty() || key.length() != 10 || key.startsWith("0")){
            logger.info(ResponseMessages.INVALID_PHONE_NUMBER);
            throw new BadRequestException(ResponseMessages.INVALID_PHONE_NUMBER);
        }

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

            if (Objects.isNull(searchProjectionList) || searchProjectionList.isEmpty()){
                logger.info(ResponseMessages.NO_RECORDS_FOUND);
                throw new NotFoundException(ResponseMessages.NO_RECORDS_FOUND);
            }

            searchProjectionList.getContent().forEach(searchProjection -> {
                SearchResult searchResult = new SearchResult();
                BeanUtils.copyProperties(searchProjection, searchResult);
                searchResultList.add(searchResult);
            });

            logger.info(ResponseMessages.SEARCH_BY_NUMBER_RESPONSE);
            return new ResponseEntity<>(new SearchResponse(searchResultList, searchProjectionList.getTotalElements())
                    , HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> getNumberDetails(String phone, CustomUserDetail userDetail)
            throws BadRequestException {

        logger.info("Inside UserServiceImpl.getNumberDetails().......");

        if (phone.trim().isEmpty() || phone.length() != 10){
            logger.info(ResponseMessages.INVALID_PHONE_NUMBER);
            throw new BadRequestException(ResponseMessages.INVALID_PHONE_NUMBER);
        }

        List<SearchResult> searchResultList = new ArrayList<>();
        Long spamCount = spamRepository.getSpamCount(phone.trim());
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isPresent()) {
            SearchResult searchResult = new SearchResult(user.get().getName(),
                    user.get().getPhone(), spamCount);

            if (inPersonsContacts(user.get(), userDetail)){
                logger.info(ResponseMessages.IN_CONTACT);

                // if there is no email for the account, return empty string
                searchResult.setEmail(user.get().getEmail() == null ? "" : user.get().getEmail());
            }

            searchResultList.add(searchResult);
        } else {
            List<SearchProjection> contacts = contactsRepository.findByPhone(phone);
            if (Objects.isNull(contacts) || contacts.isEmpty()){
                logger.info(ResponseMessages.NO_DETAILS_FOUND);
                throw new BadRequestException(ResponseMessages.NO_DETAILS_FOUND);
            }

            searchResultList = contacts.stream()
                    .map(contact -> new SearchResult(contact.getName(), contact.getPhone(), spamCount))
                    .collect(Collectors.toList());
        }

        logger.info(ResponseMessages.NUMBER_DETAIL_RESPONSE);
        return new ResponseEntity<>(new SearchResponse(searchResultList), HttpStatus.OK);
    }

    /*
    checking whether the person making this search request is in
    the contact list of the number for which the person is searching.
     */
    private boolean inPersonsContacts(User user, CustomUserDetail userDetail) {
        Optional<Contacts> contacts = contactsRepository.
                checkUsersContacts(user.getId(), userDetail.getUsername());
        return contacts.isPresent();
    }
}
