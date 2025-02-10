package com.lb.book_worm_api.exception;

public class ApiException extends RuntimeException{

    public ApiException(String message){
        super(message);
    }
}
