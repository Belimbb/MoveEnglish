package com.project.MoveEnglish.exception.generalExceptions;

public class SomethingWentWrongException extends RuntimeException {

    public SomethingWentWrongException() {
        super("Something went wrong, please contact the administrator");
    }
}
