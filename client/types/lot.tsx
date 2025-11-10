export interface Lot {
  id: number;
  name: string;
  currentVolume: number;
  capacity: number;
  street: string;
  city: string;
  state: string;
  zip: string;
  createdAt: string;
  updatedAt: string;
}

export function createLot(
    id: number, 
    name: string, 
    currentVolume: number,
    capacity: number,
    street: string,
    city: string,
    state: string,
    zip: string,
    createdAt: string,
    updatedAt: string): Lot {

    let lot: Lot = {
        id: id,
        name: name,
        currentVolume: currentVolume,
        capacity: capacity,
        street: street,
        city: city,
        state: state,
        zip: zip,
        createdAt: createdAt,
        updatedAt: updatedAt
    }

    return lot
}

export function getMockLot(): Lot {
    return { id: 0, name: "Unnamed Lot", 
            currentVolume: 0, 
            capacity: 0, 
            street: "Some Street", 
            city: "Some City", 
            state: "Some State", 
            zip: "Some Zip", 
            createdAt: new Date().toISOString(), 
            updatedAt: new Date().toISOString()
        };
}