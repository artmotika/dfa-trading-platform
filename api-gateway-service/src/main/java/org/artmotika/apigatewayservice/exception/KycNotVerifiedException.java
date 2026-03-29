package org.artmotika.apigatewayservice.exception;

public class KycNotVerifiedException extends RuntimeException {
    public KycNotVerifiedException(String m) {
        super(m);
    }
}
