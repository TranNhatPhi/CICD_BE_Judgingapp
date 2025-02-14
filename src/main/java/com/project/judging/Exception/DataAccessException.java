package com.project.judging.Exception;

import org.springframework.http.HttpStatus;

public class DataAccessException extends CustomException {
    public DataAccessException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
