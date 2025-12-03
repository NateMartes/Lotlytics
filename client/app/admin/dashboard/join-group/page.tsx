"use client"
import { ForwardedRef, forwardRef, useImperativeHandle, useMemo, useEffect, useState, useRef, FormEvent } from "react";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";
import { GroupList, GroupListHandle } from "@/components/groups";
import { createGroup, Group } from "@/types/group";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export default function JoinGroupPage() {

    const {isAuthenticated, isLoading, user} = useAuth();
    const [isSearching, setIsSearching] = useState<boolean>(false);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [searchInput, setSearchInput] = useState<string>("");
    const groupListHandle = useRef<GroupListHandle>(null);
    const router = useRouter();

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            router.push("/admin");
        } else {
            getGroups("http://localhost/api/v1/group");
        }
    }, [isAuthenticated,  isLoading, router])

    const handleSearchSubmit = (event: FormEvent) => {
        event.preventDefault();
        getGroups(`http://localhost/api/v1/group?name=${searchInput}`);
    }

    const getGroups = async (link: string) => {

        fetch(link, {
            credentials: "include"
        })
        .then( async (res: Response) => {
            if (!res.ok) {
                throw new Error(`Failed to get groups, status: ${res.status}`);
            } else {
                let body: Group[] = await res.json();
                setErrorMessage(null);
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
                <div className="w-full p-4 flex flex-col place-items-center">
                    <p className="text-left text-2xl lg:text-3xl w-110 md:w-125 mb-4">Join a Group</p>
                    <p className="text-left text-lg w-110 md:w-125">Can't find a group? <a className="hover:underline" href="/admin/dashboard/create-group">Create one!</a></p>
                </div>
                <div className="w-full max-w-6xl flex flex-col gap-6 p-5 place-items-center">
                    <Card className="w-full max-w-md md:max-w-lg">
                        <form className="flex p-4 justify-center gap-4" onSubmit={(event: FormEvent) => handleSearchSubmit(event)}>
                            <Input
                            type="text"
                            value={searchInput}
                            placeholder="Search for a Group..."
                            onChange={(e) => setSearchInput(e.target.value)}
                            />
                            <Button className="bg-blue-950 hover:bg-blue-500" disabled={isSearching || searchInput === ""}>
                            {isSearching ? 'Loading...' : 'Search'}
                            </Button>
                        </form>
                    </Card>
                </div> 
                {errorMessage ? 
                    <div className="mt-2 mb-2">
                        <span className="text-red-600">{errorMessage}</span> 
                    </div>
                : null}
                <GroupList ref={groupListHandle} hasSearched={isSearching}/>
            <Footer/>
        </>
    )
}