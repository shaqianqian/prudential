package com.prudential.interview.exception;

public class CarRentalException extends RuntimeException{
    public CarRentalException(String errorMessage) {
        super(errorMessage);
    }
}
