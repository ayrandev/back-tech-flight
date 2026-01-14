package com.one.flightontime.infra.exceptions;

public class CodigoInvalidoException extends RuntimeException {
    public CodigoInvalidoException(String message) {
        super(message);
    }
}
