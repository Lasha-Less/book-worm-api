package com.lb.book_worm_api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String message;
    private LocalDateTime timestamp;
}
