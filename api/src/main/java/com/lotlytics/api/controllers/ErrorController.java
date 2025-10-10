package com.lotlytics.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.lotlytics.api.entites.ErrorMessage;

/*
 * The ErrorController Class sends error messages over HTTP
 * In the even the server suffers an internal error.
 */
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    /**
     * The handleAllExceptions method abstracts internal server execeptions
     * into internal server error HTTP responses.
     * 
     * @param ex The Exception to abstract.
     * @return A ResponseEntitly<ErrorMessage> with the 500 internal server error code.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorMessage> handleAllExceptions(Exception ex) {
        ErrorMessage error = new ErrorMessage("Server Error");
        return new ResponseEntity<ErrorMessage>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}