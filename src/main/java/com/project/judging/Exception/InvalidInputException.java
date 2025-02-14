package com.project.judging.Exception;

import org.springframework.http.HttpStatus;

class InvalidInputException extends CustomException {
    public InvalidInputException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
