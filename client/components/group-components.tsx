import { Group, GroupMemberLink } from "@/types/group";
import {
  RefObject,
  ForwardedRef,
  forwardRef,
  useImperativeHandle,
  useState,
  useMemo
} from "react";
import { Card, CardContent, CardFooter, CardHeader } from "@/components/ui/card";
import { ButtonGroup, ButtonGroupSeparator } from "@/components/ui/button-group";
import { Button } from "@/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip"
import {
  DropdownMenu,
  DropdownMenuItem,
  DropdownMenuTrigger,
  DropdownMenuLabel,
  DropdownMenuGroup,
  DropdownMenuContent,
  DropdownMenuSeparator
} from "@/components/ui/dropdown-menu"

import { API_URL } from "@/types/url";

export type GroupListHandle = {
  setGroupList: (groups: Group[]) => void;
  clearGroups: () => void;
};

export type UserGroupListHandle = {
  setUserGroupList: (groups: GroupMemberLink[]) => void;
  clearGroups: () => void;
};

/**
 * The getAllGroups functions gets all groups known in the backend.
 * 
 * @param groupListRef A group list to place all of the found groups.
 * @param callback A fucntion to run when the groups are gathered.
 * @param errorCallback A function to run when an error occurs getting groups.
 */
export function getAllGroups(
  groupListRef: RefObject<GroupListHandle | null>,
  callback: (g: Group[]) => void,
  errorCallback: (error: Error) => void,
) {
  const url = API_URL + "/group";
  fetch(url, {
    credentials: "include",
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        throw new Error(`Failed to get groups, status: ${res.status}`);
      } else {
        const body: Group[] = await res.json();
        groupListRef?.current?.setGroupList(body);
        callback(body);
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

/**
 * The addUserToGroup functinon gets all groups a user belongs to.
 * @param username The username of the user.
 * @param callback A function to call when the user is added to the group.
 * @param errorCallback A function to call when an error occurs adding the user to a group.
 */
export function getAllUserGroups(
  username: string,
  groupListRef: RefObject<UserGroupListHandle | null>,
  callback: (g: GroupMemberLink[]) => void,
  errorCallback: (error: Error) => void,
) {
  const url = API_URL + "/group/member?username=" + username;
  fetch(url, {
    method: "GET",
    credentials: "include",
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        const body = await res.json();
        throw new Error(`Failed to get user groups, status: ${JSON.stringify(body)}`);
      } else {
        const body: GroupMemberLink[] = await res.json();
        groupListRef?.current?.setUserGroupList(body);
        callback(body);
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

/**
 * The postGroupMemeber function adds a user to a group.
 * 
 * @param groupId The group to add the user to.
 * @param userId The user to add to the group.
 * @param callback A function to call when the user is successfully added to the group.
 * @param errorCallback A function to call when an error occurs while adding a user to a group.
 */
 export function postGroupMemeber(
   groupId: string, 
   userId: number, 
   callback: () => void, 
   errorCallback: (e: Error) => void
 ) {
  const url = API_URL + `/group/member?groupId=${groupId}`;
  const payload = {
     userId: userId,
  };
  
  fetch(url, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        const body = await res.json();
        throw new Error(`Failed to add user to group, status: ${JSON.stringify(body)}`);
      } else {
        callback();
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

interface GroupListProps {
  searching: boolean;
  username: string;
  userGroups?: RefObject<UserGroupListHandle | null>
  onJoin: (groupName: string) => void | undefined
}

interface userGroupsDropDownProps {
  searching: boolean;
  onSelect: (groupId: string) => void;
}

function capitalizeFirstLetter(val: string) {
  return String(val).charAt(0).toUpperCase() + String(val).slice(1);
}

function handleJoinGroup(
  group: Group, 
  userId: number | undefined,
  onJoin: (groupName: string) => void | undefined
) {
  if (userId === undefined) {
    return;
  }
  
  postGroupMemeber(group.id, userId,
    () => {
      if (onJoin != undefined) {
        onJoin(group.name);
      }
    },
    (e: Error) => console.error(e.message)
  );
}

function getGroupComponent(
  group: Group, 
  userId: number | undefined, 
  isUserGroup: boolean, 
  onJoin: (groupName: string) => void | undefined
) {
  return (
    <Card className="w-75 p-3 flex flex-col" key={group.id}>
      <CardHeader className="text-xl">
        {capitalizeFirstLetter(group.name)}
      </CardHeader>
      <CardContent className="flex-1">
        <p className="text-sm text-muted-foreground font-medium">
          ID: {group.id}
        </p>
        {isUserGroup ?
          <div className="h-10 mt-2">
            <p className="text-sm text-red-500 font-medium">
              You are already a member of this group.
            </p>
          </div>
        : null}
      </CardContent>
      <CardFooter>
        <ButtonGroup>
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                size="sm"
                className="bg-blue-950 text-white hover:bg-green-500"
                onClick={() => handleJoinGroup(group, userId, onJoin)}
                title={isUserGroup === true ? "You are already a member of this group" : `Join the ${group.name} group`}
                disabled={isUserGroup}
              >
                Join
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <p>{`Join ${group.name}'s group`}</p>
            </TooltipContent>
          </Tooltip>
          <ButtonGroupSeparator />
          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                size="sm"
                className="bg-blue-950 text-white hover:bg-blue-500"
              >
                View Lots
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <p>{`View ${group.name}'s parking lots`}</p>
            </TooltipContent>
          </Tooltip>
        </ButtonGroup>
      </CardFooter>
    </Card>
  );
}

function GroupListComponent(
  { searching, userGroups, onJoin }: GroupListProps,
  groups: ForwardedRef<GroupListHandle>,
) {
  
  const [groupList, setGroupList] = useState<Group[]>([]);
  const [userGroupList, setUserGroupList] = useState<GroupMemberLink[]>([]);
  const userGroupIds = useMemo(() => {
    return new Set(userGroupList.map((g: GroupMemberLink) => g.groupId));
  }, [userGroupList]);
  const userId = useMemo(() => {
    if (userGroupList.length > 0) {
      return userGroupList[0].userId;
    }
  }, [userGroupList]);
  
  useImperativeHandle(
    groups,
    () => ({
      setGroupList: (incomingGroups: Group[]) => {
        setGroupList(incomingGroups ?? []);
      },
      clearGroups: () => {
        setGroupList([]);
      },
    }),
    [],
  );
  
  useImperativeHandle(
    userGroups,
    () => ({
      setUserGroupList: (incomingGroups: GroupMemberLink[]) => {
        setUserGroupList(incomingGroups ?? []);
      },
      clearGroups: () => {
        setUserGroupList([]);
      },
    }),
    [],
  );
  
  return (
    <div className="w-screen flex flex-col gap-6 p-5 place-items-center">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 items-stretch">
        {groupList.map((group: Group) => getGroupComponent(group, userId, userGroupIds.has(group.id), onJoin))}
      </div>
      {!searching && groupList.length == 0 ? (
        <p className="text-center text-base text-muted-foreground">
          No parking groups just yet...
        </p>
      ) : null}
    </div>
  );
}

export const GroupList = forwardRef<GroupListHandle, GroupListProps>(
  GroupListComponent,
);

function getUserGroupDropDownComponent(groupId: string, onSelect: (groupId: string) => void) {
  return (
    <DropdownMenuItem key={groupId} onClick={() => onSelect(groupId)}>
      {groupId}
    </DropdownMenuItem>
  );
}

function UserGroupDropDownComponent(
    { onSelect }: userGroupsDropDownProps,
    userGroups: ForwardedRef<UserGroupListHandle>,
  ) {
    
  const [userGroupList, setUserGroupList] = useState<GroupMemberLink[]>([]);
    
  useImperativeHandle(
    userGroups,
    () => ({
      setUserGroupList: (incomingGroups: GroupMemberLink[]) => {
        setUserGroupList(incomingGroups ?? []);
      },
      clearGroups: () => {
        setUserGroupList([]);
      },
    }),
    [],
  );
    
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button className="bg-blue-950 text-white hover:bg-blue-500">Select a Group</Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuLabel>Your Groups</DropdownMenuLabel>
        <DropdownMenuSeparator/>
        <DropdownMenuGroup>
          {userGroupList.map((groupMemberLink: GroupMemberLink) => getUserGroupDropDownComponent(groupMemberLink.groupId, onSelect))}
        </DropdownMenuGroup>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}

export const UserGroupDropDown = forwardRef<UserGroupListHandle, userGroupsDropDownProps>(
  UserGroupDropDownComponent,
);

