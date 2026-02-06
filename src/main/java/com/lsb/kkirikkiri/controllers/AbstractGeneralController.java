package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.results.Result;

import java.util.HashMap;
import java.util.Map;

public class AbstractGeneralController {
    protected AbstractGeneralController() {
        super();
    }

    protected Map<String, Object> prepareJsonResponse(Result result) {
        Map<String, Object> response = new HashMap<>();
       if (result instanceof Enum<?>) {
           response.put("result", ((Enum<?>) result).name());
       } else {
           response.put("result", result.toString());
       }

       return response;
    }
}
