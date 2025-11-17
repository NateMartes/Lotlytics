package com.lotlytics.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import lombok.Getter;
import lombok.Setter;
import com.lotlytics.api.entites.geocoding.ValidatedLotAddressUSA;

/*
 * The OpenStreetMapService calls out to the nominatim.openstreetmap.org API
 * for address validation.
 */
@Service
public class OpenStreetMapService implements GeocodingService {

    private static String API_ENDPOINT = "https://nominatim.openstreetmap.org/search";

    @Getter
    @Setter
    /*
     * The Address class represents a address response from OpenStreetMap
     */
    private static class Address {
        private String house_number;
        private String road;
        private String city;
        private String state;
        private String postcode;
        private String country_code;

        public String getFullStreet() {

            if (house_number == null) {
                if (road == null) {
                    return "";
                } else {
                    return road;
                }
            } else {
                if (road == null) {
                    return house_number;
                } else {
                    return house_number + " " + road;
                }
            }
        }
    }

    @Getter
    @Setter
    /*
     * The Result class represents a singular result object from OpenStreetMap
     */
    private static class Result {
        private String display_name;
        private String lat;
        private String lon;
        private Address address;
    }

    /**
     * The getURL method takes a possible address and returns a URL with the OpenStreetMap API
     * 
     * @param street
     * @param city
     * @param state
     * @param zip
     * @return a String representing the API url with query parameters
     */
    private String getURL(String street, String city, String state, String zip) {
        
            String fullAddString = String.format("%s,%s,%s,%s", street, city, state, zip);
            return UriComponentsBuilder
                    .fromUriString(API_ENDPOINT)
                    .queryParam("format", "json")
                    .queryParam("addressdetails",1)
                    .queryParam("q", fullAddString)
                    .build()
                    .toUriString();
    }

    /**
     * The validateAddressInUS is implemented from the GeocodingService interface.
     */
    @Override
    public ValidatedLotAddressUSA validateAddressInUS(String street, String city, String state, String zip) {
        String url = getURL(street, city, state, zip);
        Result[] results = new RestTemplate().getForObject(url, Result[].class);
        if (results == null || results.length == 0) {
            return new ValidatedLotAddressUSA(false);
        }

        Result firstResult = results[0];
        Address firstResultAddress = firstResult.getAddress();
        if (!firstResultAddress.country_code.equals("us")) {
            return new ValidatedLotAddressUSA(true, false);
        }

        return new ValidatedLotAddressUSA(
            true,
            true,
            firstResultAddress.getFullStreet(),
            firstResultAddress.getCity(),
            firstResultAddress.getState(),
            firstResultAddress.getPostcode(),
            firstResult.getLat(),
            firstResult.getLon()
        );
    }
}
