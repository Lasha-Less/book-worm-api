package com.lb.book_worm_api.exception;

public class ResourceNotFoundException extends ApiException{

    public ResourceNotFoundException(String message){
        super(message);
    }
}
