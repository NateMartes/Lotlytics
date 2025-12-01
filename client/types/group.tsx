export interface Group {
  id: number;
  name: string;
}

export function createGroup(
    id: number,
    name: string, 
    ): Group {

    let group: Group = {
        id: id,
        name: name,
    }

    return group
}

export function createMockGroup(): Group {
    return { id: 0,
            name: "Unnamed group"
        };
}