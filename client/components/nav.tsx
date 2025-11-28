"use client"

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu"
import Image from 'next/image';
import { useAuth } from '@/context/AuthContext';
import { LogIn, LogOut, Search, User } from "lucide-react";
import { useRouter } from "next/navigation";

interface NavProps {
  isMain?: boolean
  hasIcon?: boolean
}

export function Navigation({ isMain = true, hasIcon = true }: NavProps) {

  const { isLoading, isAuthenticated, user, refreshUser } = useAuth();
  const router = useRouter();
  
  const logoutUser = () => {

    const url = "http://localhost/api/v1/user/logout";
    
    fetch(url, {
      credentials: "include",
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      }})
      .then(async (res: Response) => {
        if (!res.ok) {
          let val = await res.json();
          console.log(val);
          throw new Error(`Logout failed. Status: ${res.status}`);
        } else {
          await refreshUser();
          router.push("/");
        }
      })
      .catch((error: Error) => {
        console.error("Logout error:", error);
      })
  }
  const loginLink = (
      <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin">
        <LogIn />
        <span className="md:inline hidden">Log In</span>
      </a>
  )

  const userDisplay = isAuthenticated && user ? (
      <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" onClick={logoutUser}>
        <LogOut />
        <span className="md:inline hidden">Log Out</span>
      </a>
    ) : (
      loginLink
    );

  const isMainClass = isMain ? "shadow-md" : ""
    
  return (
    <nav className={`flex sticky max-h-20 top-0 text-md md:text-2xl text-white p-5 justify-between w-full bg-blue-950 ${isMainClass} z-1002`}>
      {hasIcon ? <div>
        <a href="/">
          <Image src="/Lotlytics.avif" alt="Lotlytics" width="60" height="60"/>
        </a>
      </div>: <div></div>}
      <NavigationMenu>
        <NavigationMenuList className="md:min-w-md flex justify-end gap-6">
          <NavigationMenuItem>
              <a className="flex place-items-center gap-2 p-2 hover:bg-primary/30 rounded-lg transition-all" href="/">
                <Search />
                <span className="md:inline hidden">Search</span>
              </a>
          </NavigationMenuItem>
          <NavigationMenuItem>
              <a className="flex place-items-center gap-2 p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin/dashboard">
                <User />
                <span className="md:inline hidden">My Dashboard</span>
              </a>
          </NavigationMenuItem>
          <NavigationMenuItem>
            {userDisplay}          
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  )
}
