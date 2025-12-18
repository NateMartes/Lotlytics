export interface Group {
  id: string;
  name: string;
}

export interface GroupMemberLink {
  id: number;
  groupId: string;
  userId: number;
  roleId: number;
}

export function createGroup(id: string, name: string): Group {
  const group: Group = {
    id: id,
    name: name,
  };

  return group;
}

export function createMockGroup(): Group {
  return { id: "mygroup-abc123", name: "Unnamed group" };
}
