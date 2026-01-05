package com.example.demo.excpetions;
public class DownstreamServiceUnavailableException extends RuntimeException {

    public DownstreamServiceUnavailableException(String message) {
        super(message);
    }
}
