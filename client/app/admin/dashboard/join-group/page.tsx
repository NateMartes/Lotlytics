"use client"
import { ForwardedRef, forwardRef, useImperativeHandle, useMemo, useEffect, useState, useRef } from "react";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";
import { GroupList, GroupListHandle } from "@/components/groups";
import { createGroup, Group } from "@/types/group";

export default function JoinGroupPage() {

    const {isAuthenticated, isLoading, user} = useAuth();
    const [isSearching, setIsSearching] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const groupListHandle = useRef<GroupListHandle>(null);
    const router = useRouter();

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            router.push("/admin");
        } else {
            getGroups();
        }
    }, [isAuthenticated,  isLoading, router])

    const getGroups = async () => {

        const url = "http://localhost/api/v1/group"
        fetch(url, {
            credentials: "include"
        })
        .then( async (res: Response) => {
            if (!res.ok) {
                throw new Error(`Failed to get groups, status: ${res.status}`);
            } else {
                let body: Group[] = await res.json();
                groupListHandle.current?.setGroupList(body);
            }
        })
        .catch((error: Error) => {
            console.error(error);
            setErrorMessage("Failed to gather parking groups, please try again.");
        })
        .finally(() => {
            setIsSearching(false);
        });
    };

    return (
        <>
            <Navigation/>
                <GroupList ref={groupListHandle} hasSearched={isSearching}/>
            <Footer/>
        </>
    )
}