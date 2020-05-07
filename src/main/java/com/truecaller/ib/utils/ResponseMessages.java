package com.truecaller.ib.utils;

public class ResponseMessages {

    private ResponseMessages(){}

    public static final String INVALID_PHONE_NUMBER = "Invalid phone number.";
    public static final String NAME_AND_PHONE_REQUIRED = "Name and phone are required.";
    public static final String ALREADY_REGISTERED = "This number is already registered.";
    public static final String SIGN_UP_SUCCESSFUL = "Sign up successful, returning jwt.";
    public static final String NO_SEARCH_KEY = "No search key provided.";
    public static final String NO_RECORDS_FOUND = "No records found.";
    public static final String NO_PHONE_PROVIDED = "No phone number provided.";
    public static final String NOT_REGISTERED = "You must be a registered user to report spam.";
    public static final String ALREADY_REPORTED_SPAM = "You've already reported this number as spam.";
    public static final String NO_DETAILS_FOUND = "No details found for given name and number.";
    public static final String SEARCH_BY_NAME_RESPONSE = "Returning search by name response.";
    public static final String MARKED_SPAM = "Spam marked.";
    public static final String SEARCH_BY_NUMBER_RESPONSE = "Returning search by number response.";
    public static final String NUMBER_DETAIL_RESPONSE = "Returning number detail response.";
    public static final String IN_CONTACT = "Logged in user is in contact list of requested number.";
    public static final String PAGINATION_MESSAGE = "Page no. should be greater than 0 and page size should be greater than 1";

}
