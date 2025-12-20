"use client"

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
} from "@/components/ui/sidebar";
import Link from "next/link";
import {
  Home,
  LogOut, 
  MapPlus, 
  Users 
} from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { logoutUser } from "@/components/user-components";
import { useRouter } from "next/navigation";
import Image from "next/image";

export function DashboardMenu() {
  
  const { refreshUser } = useAuth();
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
  
  return (
    <Sidebar className="border-blue-950">
      <SidebarHeader>
        <div>
          <Link href="/">
            <Image
              src="/Lotlytics.avif"
              alt="Lotlytics"
              width="60"
              height="60"
            />
          </Link>
        </div>
      </SidebarHeader>
      <SidebarContent className="text-white text-lg">
        <SidebarGroup>
          <SidebarGroupLabel className="text-white text-lg mb-5 ml-0">
            Actions
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <Link
                  className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all"
                  href="/admin/dashboard"
                >
                  <Home />
                  My Dashboard
                </Link>
              </SidebarMenuItem>
              <SidebarMenuItem>
                <Link
                  className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all"
                  href="/admin/dashboard/create-lot"
                >
                  <MapPlus />
                  Create Lot
                </Link>
              </SidebarMenuItem>
              <SidebarMenuItem>
                <Link
                  className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all"
                  href="/admin/dashboard/join-group"
                >
                  <Users />
                  Join a Parking Group
                </Link>
              </SidebarMenuItem>
              <SidebarMenuItem>
                <span className="flex gap-2 place-items-center p-2 cursor-pointer hover:bg-primary/30 rounded-lg transition-all" 
                    onClick={() => handleUserLogout()}
                  >
                    <LogOut />
                    Log Out
                  </span>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
