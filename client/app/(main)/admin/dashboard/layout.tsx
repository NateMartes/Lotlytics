import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { DashboardMenu } from "@/components/sidebar-menu-components";
import {
  Breadcrumb,
  BreadcrumbList,
  BreadcrumbItem,
  BreadcrumbPage,
} from "@/components/ui/breadcrumb";

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <SidebarProvider >
        <DashboardMenu />
        <div className="flex flex-col w-full">
          <Breadcrumb className="p-5">
            <BreadcrumbList className="flex place-items-center">
              <BreadcrumbItem>
                <SidebarTrigger/>
              </BreadcrumbItem>
              <BreadcrumbItem>
                <BreadcrumbPage>
                  <h1 className="text-xl">My Dashboard</h1>
                </BreadcrumbPage>
              </BreadcrumbItem>
            </BreadcrumbList>
          </Breadcrumb>
          { children }
        </div>
      </SidebarProvider>
    </>
  );
}
