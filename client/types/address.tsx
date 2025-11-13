export interface Address {
    street: string;
    city: string;
    state: string;
    zip: string;
}

export interface AddressObject {
    house_number: string;
    road: string;
    neighbourhood: string;
    city: string;
    state: string;
    postcode: string;
    country: string;
    country_code: string;
}

export function createAddress(
    street: string,
    city: string,
    state: string,
    zip: string,
    ): Address {

    let address: Address = {

        street: street,
        city: city,
        state: state,
        zip: zip
    }

    return address
}

export function getMockAddress(): Address {
    return {
            street: "Some Street", 
            city: "Some City", 
            state: "Some State", 
            zip: "Some Zip"
        };
}