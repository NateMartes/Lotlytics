"use client";

import { Navigation } from "@/components/nav";
import { DashboardMenu } from "@/components/menu";
import { Footer } from "@/components/footer";
import {
  Breadcrumb,
  BreadcrumbList,
  BreadcrumbItem,
  BreadcrumbPage,
} from "@/components/ui/breadcrumb";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { useEffect, useRef, useState } from "react";
import { getAllLots, LotList } from "@/components/lot-components";
import { LotListHandle } from "@/types/lot";
import { Spinner } from "@/components/ui/spinner";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";

export default function Dashboard() {
  const [searching, setSearching] = useState<boolean>(true);
  const [errorMessage, setErrorMessage] = useState<string | null>("");
  const lotListRef = useRef<LotListHandle>(null);
  const router = useRouter();
  const { isLoading, isAuthenticated } = useAuth();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      router.push("/admin");
    }
  }, [isLoading, isAuthenticated, router]);

  useEffect(() => {
    getAllLots(
      lotListRef,
      () => {
        setSearching(false);
      },
      (e: Error) => {
        setErrorMessage(e.message);
      },
    );
  }, []);

  return (
    <>
      <SidebarProvider>
        <DashboardMenu />
        <main className="flex flex-1 flex-col transition-all duration-300 ease-in-out w-full">
          <Navigation isMain={false} hasIcon={false} />
          <div className="p-5">
            <Breadcrumb className="mb-5">
              <BreadcrumbList className="flex place-items-center">
                <BreadcrumbItem>
                  <SidebarTrigger />
                </BreadcrumbItem>
                <BreadcrumbItem>
                  <BreadcrumbPage>
                    <h1 className="text-xl">My Lots</h1>
                  </BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
            <div className="flex flex-col place-items-center">
              {searching ? (
                <div className="flex place-items-center gap-5 mt-10">
                  <p>Gathering your Parking Lots</p>
                  <Spinner className="size-8" />
                </div>
              ) : null}
              {errorMessage ? (
                <div className="p-4 mt-4 text-center text-red-500 text-base">
                  {errorMessage}
                </div>
              ) : null}
              <LotList searching={searching} ref={lotListRef} />
            </div>
          </div>
          <Footer />
        </main>
      </SidebarProvider>
    </>
  );
}
