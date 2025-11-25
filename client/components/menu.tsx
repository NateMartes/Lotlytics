import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
  SidebarMenuItem,
  SidebarRail,
} from "@/components/ui/sidebar"
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
            <SidebarContent className="text-white">
                <SidebarMenuItem>
                    <a href="/admin/dashboard/create-lot">Create Lot</a>
                </SidebarMenuItem>
                <SidebarMenuItem>
                    <a href="/admin/dashboard/create-lot">Add A User to my Group</a>
                </SidebarMenuItem>
                <SidebarMenuItem>
                    <a href="/admin/dashboard/create-lot">Log Out</a>
                </SidebarMenuItem>
            </SidebarContent>
        </Sidebar>
    )
}