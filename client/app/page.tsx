"use client"
import { Input } from "@/components/ui/input"
import { Lot, createLot } from "@/types/lot";
import { LotList, LotListHandle } from "@/components/lots";
import { FormEvent, useRef, useState } from "react"
import { Spinner } from "@/components/ui/spinner";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";

export default function Home() {

  const [searchInput, setSearchInput] = useState("");
  const [searching, setSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const lotListRef = useRef<LotListHandle>(null);

  const handleSearchSubmit = (event: FormEvent) => {
    event.preventDefault();

    setSearching(true);
    const url = "http://localhost/api/v1/lot"

    fetch(url)
      .then((res: Response) => {
        if (!res.ok) {
          throw new Error(`Unable to fetch parking lots. Status: ${res.status}`);
        }
        return res.json();
      })
      .then((data: Lot[]) => {
        const lots = data.map((lot) => createLot(
          lot.id,
          lot.groupId,
          lot.name, 
          lot.currentVolume, 
          lot.capacity, 
          lot.street, 
          lot.city, 
          lot.state, 
          lot.zip, 
          lot.createdAt, 
          lot.updatedAt
        ));
        lotListRef.current?.setLots(lots);
        lotListRef.current?.setFilter("all");
        setErrorMessage(null);
      })
      .catch((error: Error) => {
        console.error("Error fetching lots:", error);
        lotListRef.current?.clearLots();
        setErrorMessage("We couldn't load parking lots right now. Please try again.");
      })
      .finally(() => {
        setSearching(false);
        setHasSearched(true);
      });
  }

  return (
    <>
      <Navigation/>
      <div className="flex flex-col place-items-center mt-5 text-2xl lg:text-3xl gap-4">
          <p className="text-center">Travel Better, Park Smarter.</p>
          <Card className="md:min-w-2xl">
            <form className="flex p-4 justify-center gap-4" onSubmit={(event: FormEvent) => handleSearchSubmit(event)}>
                <Input
                  type="text"
                  value={searchInput}
                  placeholder="Search for a Parking Lot..."
                  onChange={(e) => setSearchInput(e.target.value)}
                />
                <Button className="bg-blue-950 hover:bg-blue-500" disabled={searching}>
                  {searching ? 'Loading...' : 'Search'}
                </Button>
            </form>
          </Card>
          {searching ? 
            <div className="flex place-items-center gap-5 mt-10">
              <p className="p-4 mt-50 mb-50">Searching for Parking Lots</p>
              <Spinner className="size-12" />
            </div>
          : null} 
          {errorMessage ? (
            <div className="p-4 mt-4 text-center text-red-500 text-base">
              {errorMessage}
            </div>
          ) : null}
          <div className="p-6 w-full">
            <LotList ref={lotListRef} hasSearched={hasSearched} />
          </div>
      </div>
      <Footer/>
    </>
  );
}
