"use client"

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu"
import Image from 'next/image';
import { useAuth } from '@/context/AuthContext';

export function Navigation() {
  const { isLoading, isAuthenticated, user } = useAuth();

  const loginLink = (
      <a href="/admin">Log In</a>
  )

  const userDisplay = isAuthenticated && user ? (
      <a href="/admin/create-lot">
        <p>{user.username}'s Dashboard</p>
      </a>
    ) : (
      loginLink
    );
    
  return (
    <nav className="flex sticky top-0 text-md md:text-2xl text-white p-5 justify-between w-screen bg-blue-900 shadow-md z-1002">
      <div>
        <a href="/">
          <Image src="/Lotlytics.avif" alt="Lotlytics" width="60" height="60"/>
        </a>
      </div>
      <NavigationMenu>
        <NavigationMenuList className="md:min-w-md flex justify-end gap-4">
          <NavigationMenuItem>
              <p>About</p>
          </NavigationMenuItem>
          <NavigationMenuItem>
            {userDisplay}          
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  )
}
