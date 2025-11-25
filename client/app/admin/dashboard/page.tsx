'use client';

import { Navigation } from "@/components/nav";
import { DashboardMenu } from "@/components/menu";
import { Footer } from "@/components/footer";
import { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbPage } from "@/components/ui/breadcrumb";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";

export default function Dashboard() {
    return (
        <>
            <SidebarProvider>
                <DashboardMenu/>
                    <main className="flex flex-1 flex-col transition-all duration-300 ease-in-out w-full">
                        <Navigation isMain={false} hasIcon={false}/>
                        <div className="p-5">
                            <Breadcrumb>
                                <BreadcrumbList className="flex place-items-center">
                                    <BreadcrumbItem>
                                        <SidebarTrigger/>
                                    </BreadcrumbItem>
                                    <BreadcrumbItem>
                                        <BreadcrumbPage>
                                            <h1 className="text-xl">My Lots</h1>
                                        </BreadcrumbPage>
                                    </BreadcrumbItem>
                                </BreadcrumbList>
                            </Breadcrumb>
                        </div>
                        <Footer/>
                    </main>
            </SidebarProvider>
        </>
    );
}