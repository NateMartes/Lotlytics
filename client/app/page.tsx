"use client"
import { Input } from "@/components/ui/input"
import { Lot, createLot } from "@/types/lot";
import { LotList } from "./lots";
import { ChangeEvent, FormEvent, useState } from "react"
import { Spinner } from "@/components/ui/spinner";

export default function Home() {

  const [searchInput, setSearchInput] = useState("");
  const [searching, setSearching] = useState(false);
  const [searchResults, setSearchResults] = useState<Lot[] | null>(null);

  const handleInputUpdate = (event: ChangeEvent<HTMLInputElement>) => {
    let element = event.target;
    setSearchInput(element.value);
  }

  const handleSearchSubmit = (event: FormEvent) => {
    event.preventDefault();

    setSearching(true);
    const url = "http://localhost/api/v1/lot"

    fetch(url).then((res: Response) => {
      if (res.ok) {
        return res.json()
      }
    }).then((data: Lot[]) => {
      let lots = data.map((lot) => createLot(
        lot.id, 
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
      setSearching(false);
      setSearchResults(lots);
    });
  }

  return (
    <>
      <div className="flex flex-col place-items-center mt-5 text-2xl lg:text-3xl gap-4">
          <p className="text-center">Make Your Traveling Less Stressful</p>
          <form className="flex w-full p-4 justify-center gap-4" onSubmit={(event: FormEvent) => handleSearchSubmit(event)}>
            <Input className="max-w-lg text-xl" type="search" placeholder="Find a Parking Lot" onChange={(event: ChangeEvent<HTMLInputElement>) => handleInputUpdate(event)}/>
            <button type="submit" className="cursor-pointer">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="size-8">
                  <path strokeLinecap="round" strokeLinejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
              </svg>
            </button>
          </form>
          {searching ? 
            <div className="flex place-items-center gap-5 mt-10">
              <p>Searching for Parking Lots</p>
              <Spinner className="size-12" />
            </div>
          : null}
          {searchResults ?
            <div className="p-4">
              <LotList results={searchResults}/> 
            </div>
            : !searchResults && !searching ? 
                <div className="p-4">
                  <p>No Parking Lots Just Yet ...</p>
                </div>
                : null}
      </div>
    </>
  );
}
