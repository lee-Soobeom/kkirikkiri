package com.lsb.kkirikkiri.exceptions;

public class TransactionalException extends RuntimeException {
    public final Enum<?> result;

    public TransactionalException(Enum<?> result) {

        this.result = result;
    }
}
