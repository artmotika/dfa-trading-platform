package org.artmotika.apigatewayservice.exception;

public class AmlViolationException extends RuntimeException {
    public AmlViolationException(String m) {
        super(m);
    }
}
