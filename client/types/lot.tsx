export interface Lot {
  id: number;
  name: string;
  capacity: number;
  location: string;
}

export function createLot(id: number, name: string, location: string, capacity: number): Lot {

    let lot: Lot = {
        id: id,
        name: name,
        location: location,
        capacity: capacity
    }

    return lot
}

export function getMockLot(): Lot {
    return { id: 0, name: "Unnamed Lot", location: "Some Location", capacity: 0 };
}