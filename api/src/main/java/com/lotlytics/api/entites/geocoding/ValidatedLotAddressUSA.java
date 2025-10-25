package com.lotlytics.api.entites.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
/*
 * The ValidatedAddressUSA is an address that has been validated by a geocoding service
 */
@AllArgsConstructor
@Getter
public class ValidatedLotAddressUSA {
    private boolean valid;
    private boolean inUSA;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String lat;
    private String lon;

    /**
     * This constructor handles for when an address is invalid, then we only set
     * the valid isntance variable. This constructor should only be called when
     * a address is invalid.
     * 
     * @param valid a boolean value
     */
    public ValidatedLotAddressUSA(boolean valid) {
        this.valid = valid;
    }

    /**
     * This constructor handles for when an address is valid, but outside the USA. Then we only set
     * the valid isntance and the inUSA instance variable. This constructor should only be called when
     * a address is valid and it is outside of the USA.
     * 
     * @param valid a boolean value
     * @param inUSA a boolean value
     */
    public ValidatedLotAddressUSA(boolean valid, boolean inUSA) {
        this.valid = valid;
        this.inUSA = inUSA;
    }

    @Override
    public String toString() {
        return "ValidatedUSAddress["+ (valid && inUSA) +"]: " + street + "," + city + "," + state + "," + zip;
    }
}