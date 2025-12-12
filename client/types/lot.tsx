export interface Lot {
  id: number;
  groupId: string;
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

export type LotListHandle = {
  setLots: (lots: Lot[]) => void;
  clearLots: () => void;
  setFilter: (filter: LotFilterOption) => void;
};

export type LotOccupancyLevel = "low" | "medium" | "high";

export type LotFilterOption = "all" | LotOccupancyLevel;

export type LotLevel = {
  level: LotOccupancyLevel;
  text: string;
  color: string;
};
export function createLot(
  id: number,
  groupId: string,
  name: string,
  currentVolume: number,
  capacity: number,
  street: string,
  city: string,
  state: string,
  zip: string,
  createdAt: string,
  updatedAt: string,
): Lot {
  const lot: Lot = {
    id: id,
    groupId: groupId,
    name: name,
    currentVolume: currentVolume,
    capacity: capacity,
    street: street,
    city: city,
    state: state,
    zip: zip,
    createdAt: createdAt,
    updatedAt: updatedAt,
  };

  return lot;
}
