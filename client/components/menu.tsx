import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuItem,
  SidebarRail,
} from "@/components/ui/sidebar"
import { LogOut, MapPlus, UserPlus, Users } from "lucide-react";
import Image from 'next/image';


export function DashboardMenu() {
    return (
        <Sidebar className="border-blue-950">
            <SidebarHeader>
               <div>
                    <a href="/">
                        <Image src="/Lotlytics.avif" alt="Lotlytics" width="60" height="60"/>
                    </a>
                </div>
            </SidebarHeader>
            <SidebarContent className="text-white text-lg">
                <SidebarGroup>
                    <SidebarGroupLabel className="text-white text-lg mb-5 ml-0">Actions</SidebarGroupLabel>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            <SidebarMenuItem>
                                <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin/dashboard/create-lot"><MapPlus />Create Lot</a>
                            </SidebarMenuItem>
                            <SidebarMenuItem>
                                <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin/dashboard/create-lot"><Users />Join a Parking Group</a>
                            </SidebarMenuItem>
                            <SidebarMenuItem>
                                <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin/dashboard/create-lot"><UserPlus />Add A User to my Parking Group</a>
                            </SidebarMenuItem>
                            <SidebarMenuItem>
                                <a className="flex gap-2 place-items-center p-2 hover:bg-primary/30 rounded-lg transition-all" href="/admin/dashboard/create-lot"><LogOut />Log Out</a>
                            </SidebarMenuItem>
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
        </Sidebar>
    )
}