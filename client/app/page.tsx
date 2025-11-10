"use client"
import { Input } from "@/components/ui/input"
import { Lot, createLot } from "@/types/lot";
import { LotList } from "./lots";
import { ChangeEvent, FormEvent, useState } from "react"

export default function Home() {

  const [searchInput, setSearchInput] = useState("");
  const [searchResults, setSearchResults] = useState<Lot[] | null>(null);

  const handleInputUpdate = (event: ChangeEvent<HTMLInputElement>) => {
    let element = event.target;
    setSearchInput(element.value);
  }

  const handleSearchSubmit = (event: FormEvent) => {
    event.preventDefault();

    const url = "http://localhost/api/v1/lot?groupId=wilkes-1a2b3c4d"

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
      setSearchResults(lots);
    });
  }

  return (
    <>
      <div className="flex flex-col place-items-center mt-5 text-2xl lg:text-3xl gap-4">
          <p>Make Your Traveling Less Stressful</p>
          <form className="flex w-full justify-center gap-4" onSubmit={(event: FormEvent) => handleSearchSubmit(event)}>
            <Input className="max-w-lg text-xl" type="search" placeholder="Find a Parking Lot" onChange={(event: ChangeEvent<HTMLInputElement>) => handleInputUpdate(event)}/>
            <button type="submit" className="cursor-pointer">
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="size-8">
                  <path strokeLinecap="round" strokeLinejoin="round" d="m21 21-5.197-5.197m0 0A7.5 7.5 0 1 0 5.196 5.196a7.5 7.5 0 0 0 10.607 10.607Z" />
              </svg>
            </button>
          </form>
          {searchResults ? <LotList results={searchResults}/> : null}
      </div>
    </>
  );
}
