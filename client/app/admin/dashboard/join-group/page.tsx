"use client";
import { useEffect, useState, useRef, FormEvent } from "react";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";
import {
  getAllGroups,
  getAllUserGroups,
  GroupList,
  GroupListHandle,
  UserGroupListHandle,
} from "@/components/group-components";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTrigger,
  DialogTitle
} from "@/components/ui/dialog";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ButtonGroup, ButtonGroupSeparator } from "@/components/ui/button-group"
import Link from "next/link";

export default function JoinGroupPage() {
  const { user, isAuthenticated, isLoading } = useAuth();
  const [searching, setSearching] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState<string>("");
  const [dialogSuccessMessage, setDialogSuccessMessage] = useState<string>("You have joined a new group.");
  const groupListHandle = useRef<GroupListHandle>(null);
  const userGroupListHandle = useRef<UserGroupListHandle>(null);
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push("/admin");
    } else {
      getAllGroups(
        groupListHandle,
        () => {},
        (error: Error) => console.error(error.message),
      );
      if (user != null) { 
        getAllUserGroups(
          user.username,
          userGroupListHandle,
          () => setSearching(false),
          (error: Error) => console.error(error.message),
        );
      }
    }
  }, [isAuthenticated, isLoading, router, user]);

  const handleSearchSubmit = (event: FormEvent) => {
    event.preventDefault();
    getAllGroups(
      groupListHandle,
      () => {
        setErrorMessage(null);
        setSearching(false);
      },
      (error: Error) => setErrorMessage(error.message),
    );
  };

  return (
    <>
      <Navigation />
      <div className="w-full flex justify-center">
        <div className="text-left p-4 flex flex-col">
          <h1 className="text-2xl lg:text-3xl md:w-125 mb-4">Join a Group</h1>
          <p className="text-lg">
            {"Can't find a group? "}
            <Link
              className="hover:underline"
              href="/admin/dashboard/create-group"
            >
              Create one!
            </Link>
          </p>
        </div>
      </div>
      <div className="w-full flex flex-col place-items-center p-4">
        <Card className="w-full max-w-md md:max-w-lg">
          <form
            className="flex p-4 justify-center gap-4"
            onSubmit={(event: FormEvent) => handleSearchSubmit(event)}
          >
            <Input
              type="text"
              value={searchInput}
              placeholder="Search for a Group..."
              onChange={(e) => setSearchInput(e.target.value)}
            />
            <Button
              className="bg-blue-950 hover:bg-blue-500"
              disabled={searching || searchInput === ""}
            >
              {searching ? "Loading..." : "Search"}
            </Button>
          </form>
        </Card>
      </div>
      {errorMessage ? (
        <div className="mt-2 mb-2 text-center">
          <span className="text-red-600">{errorMessage}</span>
        </div>
      ) : null}
      <GroupList 
        ref={groupListHandle} 
        userGroups={userGroupListHandle} 
        searching={searching} 
        username={user?.username || ""} 
        onJoin={(groupName: string) => {
          setDialogSuccessMessage(`You have joined the ${groupName} group.`); 
          const dialog: HTMLElement | null = document.getElementById("joinGroupDialog");
          dialog?.click();
      }}/>
        <Dialog>
          <DialogTrigger
            className="hidden"
            id="joinGroupDialog"
          ></DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Added to Group!</DialogTitle>
              <DialogDescription id="joinGroupDialogDescription">
               {dialogSuccessMessage}
              </DialogDescription>
            </DialogHeader>
            <DialogFooter>
              <ButtonGroup>
                 <DialogClose asChild>
                  <Button
                    size="sm"
                    variant="outline"
                    className="bg-blue-950 text-white hover:bg-blue-500"
                  >
                    Close
                  </Button>
                 </DialogClose>
                <ButtonGroupSeparator />
                <Button
                  size="sm"
                  variant="outline"
                  className="bg-blue-950 text-white hover:bg-blue-500"
                  onClick={() => router.push("/")}
                >
                  Home
                </Button>
              </ButtonGroup>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      <Footer />
    </>
  );
}
