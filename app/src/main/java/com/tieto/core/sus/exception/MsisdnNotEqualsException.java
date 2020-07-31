package com.tieto.core.sus.exception;

import lombok.Getter;

@Getter
public class MsisdnNotEqualsException extends RuntimeException {
    private final String storedMsisdn;
    private final String providedMsisdn;

    public MsisdnNotEqualsException(String storedMsisdn, String providedMsisdn) {
        super("Msisdn not equals");
        this.storedMsisdn = storedMsisdn;
        this.providedMsisdn = providedMsisdn;
    }
}
