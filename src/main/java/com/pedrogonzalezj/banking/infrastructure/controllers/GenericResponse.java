
package com.pedrogonzalezj.banking.infrastructure.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class GenericResponse <T> {
    private Error error;

    private T data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {
        private String message;
        private String type;
    }
}
