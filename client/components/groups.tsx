import { Group } from "@/types/group";
import { ForwardedRef, forwardRef, useImperativeHandle, useState } from "react";
import { Card, CardContent, CardFooter, CardHeader } from "./ui/card";
import { ButtonGroup, ButtonGroupSeparator } from "./ui/button-group";
import { Button } from "./ui/button";

 export type GroupListHandle = {
    setGroupList: (groups: Group[]) => void;
    clearGroups: () => void;
};

interface GroupListProps {
    hasSearched: boolean;
}

function capitalizeFirstLetter(val: string) {
    return String(val).charAt(0).toUpperCase() + String(val).slice(1);
}

function getGroupComponent(group: Group) {
    return (
        <Card className="w-75 md:w-100 p-3" key={group.id}>
            <CardHeader className="text-xl">{capitalizeFirstLetter(group.name)}</CardHeader>
            <CardContent>
                <p className="text-sm text-muted-foreground font-medium">ID: {group.id}</p>
            </CardContent>
            <CardFooter>
                <ButtonGroup>
                    <Button size="sm" className="bg-blue-950 text-white hover:bg-green-500">Join</Button>
                    <ButtonGroupSeparator/>
                    <Button size="sm" className="bg-blue-950 text-white hover:bg-blue-500">View Lots</Button>
                </ButtonGroup>
            </CardFooter>
        </Card>
    );
}

function GroupListComponent({ hasSearched }: GroupListProps, ref: ForwardedRef<GroupListHandle>) {
    const [groupList, setGroupList] = useState<Group[]>([]);

    useImperativeHandle(ref, () => ({
        setGroupList: (incomingGroups: Group[]) => {
            setGroupList(incomingGroups ?? []);
        },
        clearGroups: () => {
            setGroupList([]);
        }
    }), []);
    
    return (
        <div className="w-full max-w-6xl flex flex-col gap-6 p-5 place-items-center">
            {groupList.map((group) => getGroupComponent(group))}
            {!hasSearched && groupList.length == 0 ? (
                <p className="text-center text-base text-muted-foreground">No parking groups just yet...</p>) : null}
        </div>
    );
}

export const GroupList = forwardRef<GroupListHandle, GroupListProps>(GroupListComponent);