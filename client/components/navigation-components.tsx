"use client";

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuList,
} from "@/components/ui/navigation-menu";
import Image from "next/image";
import Link from "next/link";
import { Search, Home, UserPlus } from "lucide-react";

export function Navigation() {
  
  return (
    <nav
      className={`flex place-items-center sticky max-h-20 top-0 text-md md:text-xl text-white p-5 justify-between w-full bg-blue-950 z-1002`}
    >
      <div>
        <Link href="/" className="flex place-items-center gap-2 rounded-lg transition-all">
          <Image
            src="/Lotlytics.avif"
            alt="Lotlytics"
            width="80"
            height="80"
            loading="eager"
          />
          <span className="md:inline hidden">Lotlytics</span>
        </Link>
      </div>
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
              href="/create-account"
            >
              <UserPlus />
              <span className="md:inline hidden">Create an Account</span>
            </Link>
          </NavigationMenuItem>
          <NavigationMenuItem>
            <Link
              className="flex place-items-center gap-2 p-2 hover:bg-primary/30 rounded-lg transition-all"
              href="/admin/dashboard"
            >
              <Home />
              <span className="md:inline hidden">My Dashboard</span>
            </Link>
          </NavigationMenuItem>
        </NavigationMenuList>
      </NavigationMenu>
    </nav>
  );
}
