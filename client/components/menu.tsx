import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarHeader,
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
        </Sidebar>
    )
}