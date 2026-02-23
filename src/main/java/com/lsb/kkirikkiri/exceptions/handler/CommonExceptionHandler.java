package com.lsb.kkirikkiri.exceptions.handler;

import com.lsb.kkirikkiri.exceptions.TransactionalException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(TransactionalException.class)
    public Map<String, Object> handleTransactionalException(TransactionalException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", e.result);
        return response;
    }
}
