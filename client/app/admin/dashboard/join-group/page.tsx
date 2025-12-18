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
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function JoinGroupPage() {
  const { user, isAuthenticated, isLoading } = useAuth();
  const [searching, setSearching] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [searchInput, setSearchInput] = useState<string>("");
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
  }, [isAuthenticated, isLoading, router]);

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
      <GroupList ref={groupListHandle} userGroups={userGroupListHandle} searching={searching} username={user?.username || ""}/>
      <Footer />
    </>
  );
}
