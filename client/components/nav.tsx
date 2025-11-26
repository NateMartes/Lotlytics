"use client"

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu"
import Image from 'next/image';
import { useAuth } from '@/context/AuthContext';
import { LogIn, LogOut, Search } from "lucide-react";

interface NavProps {
  isMain?: boolean
  hasIcon?: boolean
}

export function Navigation({ isMain = true, hasIcon = true }: NavProps) {

  const { isLoading, isAuthenticated, user } = useAuth();
  const loginLink = (
      <a className="flex gap-2 place-items-center" href="/admin"><LogIn />Log In</a>
  )

  const userDisplay = isAuthenticated && user ? (
      <a className="flex gap-2 place-items-center" href="/admin/dashboard/create-lot"><LogOut />Log Out</a>
    ) : (
      loginLink
    );

  const isMainClass = isMain ? "shadow-md" : ""
    
  return (
    <nav className={`flex sticky top-0 text-md md:text-2xl text-white p-5 justify-between w-full bg-blue-950 ${isMainClass} z-1002`}>
      {hasIcon ? <div>
        <a href="/">
          <Image src="/Lotlytics.avif" alt="Lotlytics" width="60" height="60"/>
        </a>
      </div>: <div></div>}
      <NavigationMenu>
        <NavigationMenuList className="md:min-w-md flex justify-end gap-6">
          <NavigationMenuItem>
              <a className="flex place-items-center gap-2" href="/"><Search />Search</a>
          </NavigationMenuItem>
          <NavigationMenuItem>
              <a>About</a>
          </NavigationMenuItem>
          <NavigationMenuItem>
            {userDisplay}          
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  )
}
