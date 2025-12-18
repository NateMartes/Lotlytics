"use client";

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu";
import Image from "next/image";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import { LogIn, LogOut, Search, User } from "lucide-react";
import { useRouter } from "next/navigation";
import { logoutUser } from "./user-components";

interface NavProps {
  isMain?: boolean;
  hasIcon?: boolean;
}

export function Navigation({ isMain = true, hasIcon = true }: NavProps) {
  const { isAuthenticated, user, refreshUser } = useAuth();
  const router = useRouter();

  const handleUserLogout = () => {
    logoutUser(
      async () => {
        await refreshUser();
        router.push("/");
      },
      (error: Error) => {
        console.error(error);
      },
    );
  };

  const loginLink = (
    <a
      className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all"
      href="/admin"
    >
      <LogIn />
      <span className="md:inline hidden">Log In</span>
    </a>
  );

  const userDisplay =
    isAuthenticated && user ? (
      <span
        className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all"
        onClick={handleUserLogout}
      >
        <LogOut />
        <span className="md:inline hidden">Log Out</span>
      </span>
    ) : (
      loginLink
    );

  const isMainClass = isMain ? "shadow-md" : "";

  return (
    <nav
      className={`flex place-items-center sticky max-h-20 top-0 text-md md:text-2xl text-white p-5 justify-between w-full bg-blue-950 ${isMainClass} z-1002`}
    >
      {hasIcon ? (
        <div>
          <Link href="/" className="flex place-items-center gap-2 rounded-lg transition-all">
            <Image
              src="/Lotlytics.avif"
              alt="Lotlytics"
              width="80"
              height="80"
            />
            <span className="md:inline hidden">Lotlytics</span>
          </Link>
        </div>
      ) : (
        <div></div>
      )}
      <NavigationMenu>
        <NavigationMenuList className="md:min-w-md flex justify-end gap-6">
          <NavigationMenuItem>
            <Link
              className="flex place-items-center gap-2 p-2 hover:bg-primary/30 rounded-lg transition-all"
              href="/"
            >
              <Search />
              <span className="md:inline hidden">Search</span>
            </Link>
          </NavigationMenuItem>
          <NavigationMenuItem>
            <Link
              className="flex place-items-center gap-2 p-2 hover:bg-primary/30 rounded-lg transition-all"
              href="/admin/dashboard"
            >
              <User />
              <span className="md:inline hidden">My Dashboard</span>
            </Link>
          </NavigationMenuItem>
          <NavigationMenuItem>{userDisplay}</NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  );
}
