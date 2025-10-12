package com.lotlytics.api.services;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.geocoding.ValidatedLotAddressUSA;

/*
 * The GeocodingService interface is used to provide methods for validating an address is a valid address
 */
@Service
public interface GeocodingService {

    /**
     * The validateAddressInUS determines if a given address exists within the United States.
     * 
     * @param street A possible street
     * @param city A possible city
     * @param state A possible state
     * @param zip A possible zip code
     * @return A ValidatedLotAddressUSA object
     */
    public ValidatedLotAddressUSA validateAddressInUS(String street, String city, String state, String zip);
}
