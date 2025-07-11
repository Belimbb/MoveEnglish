package com.project.MoveEnglish.exception.generalExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CustomAlreadyExistException extends RuntimeException {

    private static final String ALREADY_EXIST_EXCEPTION_NAME = "This %s already exist. Id = %s";
    private static final String ALREADY_EXIST_EXCEPTION_PROPERTY_NAME_AND_VALUE = "This %s already exist. %s = %s";

    public CustomAlreadyExistException(String objectName, Long name) {
        super(String.format(ALREADY_EXIST_EXCEPTION_NAME, objectName, name));
    }

    public CustomAlreadyExistException(String objectName, String propertyName, String value) {
        super(String.format(ALREADY_EXIST_EXCEPTION_PROPERTY_NAME_AND_VALUE, objectName, propertyName, value));
    }

    public CustomAlreadyExistException(String message) {
        super(message);
    }
}
