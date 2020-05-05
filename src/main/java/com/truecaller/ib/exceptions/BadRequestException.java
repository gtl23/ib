package com.truecaller.ib.exceptions;

public class BadRequestException extends Exception {

    final String errorMsg;

    public BadRequestException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
