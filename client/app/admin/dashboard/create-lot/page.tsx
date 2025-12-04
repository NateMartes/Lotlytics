'use client';

import { Suspense } from "react";
import { ChangeEvent, useState, useEffect, FormEvent } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { MapComponent } from "@/components/open-source-map"
import { Address } from "@/types/address"
import { useSearchParams } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { useRouter } from "next/navigation";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";
import { Dialog, 
        DialogClose, 
        DialogContent, 
        DialogDescription, 
        DialogFooter, DialogHeader, 
        DialogTrigger } 
from "@/components/ui/dialog";
import { DialogTitle } from "@radix-ui/react-dialog";
import { ButtonGroup, ButtonGroupSeparator } from "@/components/ui/button-group";

class NotANumberError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export function CreateLotForm() {
    const [name, setName] = useState<string>("");
    const [capacity, setCapacity] = useState<number>(0);
    const [volume, setVolume] = useState<number>(0);
    const [street, setStreet] = useState<string | null>("");
    const [city, setCity] = useState<string | null>("");
    const [state, setState] = useState<string | null>("");
    const [zip, setZip] = useState<string>("");
    const [formErrorMessage, setFormErrorMessage] = useState<string | null>(null);
    const [isValidVolume, setIsValidVolume] = useState<boolean>(true);
    const [isValidCapacity, setIsValidCapacity] = useState<boolean>(true);    
    const isValidForm = !isValidVolume || !isValidCapacity;
    const groupId = "google-5e6f7g8h"

    const searchParams = useSearchParams();
    const router = useRouter();
    const { isLoading, isAuthenticated } = useAuth();

    useEffect(() => {
        if (!isLoading && !isAuthenticated) {
            router.push("/admin");
        }
    }, [isLoading, isAuthenticated, router]);


    const validateVolumeInput = (value: string) => {
        try {
            let valueInt = parseInt(value);
            if (isNaN(valueInt) || valueInt < 0) {
                setIsValidVolume(false);
                throw new NotANumberError("Volume must be a natural number");
            }
            setIsValidVolume(true);
            setVolume(valueInt);
            setFormErrorMessage(null);
        } catch (error: any) {
            if (error instanceof NotANumberError) {
                setFormErrorMessage(error.message);
            } else {
                console.error(error.message);
            }
        }
    };

    const validateCapacityInput = (value: string) => {
        try {
            let valueInt = parseInt(value);
            if (isNaN(valueInt) || valueInt < 0) {
                setIsValidCapacity(false);
                throw new NotANumberError("Capacity must be a natural number.");
            }
            if (valueInt < 1) {
                setIsValidCapacity(false);
                throw new NotANumberError("Capacity must be greater than 0.");
            }
            if (valueInt < volume) {
                setIsValidCapacity(false);
                throw new NotANumberError("Capacity must be greater than or equal to the volume.");
            }
            setIsValidCapacity(true);
            setCapacity(valueInt);
            setFormErrorMessage(null);
        } catch (error: any) {
            if (error instanceof NotANumberError) {
                setFormErrorMessage(error.message);
            } else {
                console.error(error.message);
            }
        }
    };

    const handleNewAddress = (address: Address) => {
        setStreet(address.street);
        setCity(address.city);
        setState(address.state);
        setZip(address.zip);
    }

    const handleCreateLotSubmit = async (e: FormEvent) => {
        e.preventDefault();
        if (groupId === null) {
            console.error("Group ID not present in url");
            return
        }
        const payload = {
            name,
            capacity,
            volume,
            street,
            city,
            state,
            zip
        };

        try {
            const response = await fetch(`https://lotlytics-api.nathanielmartes.com/api/v1/?groupId=${groupId}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const error = await response.json();
                console.error("Error creating lot:", error);
                return;
            } else {
                let dialog: HTMLElement| null = document.getElementById("lotCreatedDialog");
                if (dialog) {
                    dialog.click();
                }
            }

        } catch (err) {
            console.error("Network error:", err);
        }
    };

    const routeHome = (e: FormEvent) => {
        e.preventDefault();
        router.push("/");
    }
    return (
        <>
            <Navigation/>
            <div className="flex justify-center">
                <form className="p-4" onKeyDown={(e) => e.key == "Enter" && e.preventDefault()} onSubmit={handleCreateLotSubmit}>
                    <h1 className="text-2xl mb-4">Define a New Parking Lot</h1>
                    <div className="flex flex-col">
                        <div className="flex flex-col max-w-lg gap-4 mt-5 mb-5">               
                            <div className="flex flex-col gap-2">
                                <label htmlFor="lotName" className="text-sm font-medium">
                                    Lot Name
                                </label>
                                <Input
                                    id="lotName" 
                                    required 
                                    placeholder="Enter your lot's name" 
                                    type="text" 
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => setName(event.target.value)}
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <label htmlFor="lotVolume" className="text-sm font-medium">
                                    Current Volume
                                </label>
                                <Input
                                    id="lotVolume"
                                    required 
                                    placeholder="Enter your lot's current volume" 
                                    type="text" 
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => validateVolumeInput(event.target.value)}
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <label htmlFor="lotCap" className="text-sm font-medium">
                                    Max Capacity
                                </label>
                                <Input
                                    id="lotCap"
                                    required 
                                    placeholder="Enter the max capacity for your lot" 
                                    type="text" 
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => validateCapacityInput(event.target.value)}
                                />
                            </div>
                        </div>
                        <div className="flex flex-col max-w-lg gap-4 mt-5 mb-5">
                            <MapComponent onAddress={handleNewAddress}/>     
                            <p>Values can only be autofilled by searching for a lot.</p>                   
                            <div className="grid grid-cols-2 gap-2">
                                <Input
                                    className="disabled:opacity-100"
                                    required
                                    placeholder="Street" 
                                    type="text"
                                    value={street? street : ""}
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => setStreet(event.target.value)}
                                    readOnly
                                    disabled
                                />
                                <Input
                                    className="disabled:opacity-100"
                                    required
                                    placeholder="City" 
                                    type="text" 
                                    value={city ? city: ""}
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => setCity(event.target.value)}
                                    readOnly
                                    disabled
                                />
                                <Input
                                    className="disabled:opacity-100"
                                    required
                                    placeholder="State" 
                                    type="text" 
                                    value={state ? state : ""}
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => setState(event.target.value)}
                                    readOnly
                                    disabled
                                />
                                <Input
                                    className="disabled:opacity-100"
                                    required 
                                    placeholder="ZIP" 
                                    type="text" 
                                    value={zip}
                                    onChange={(event: ChangeEvent<HTMLInputElement>) => setZip(event.target.value)}
                                    readOnly
                                    disabled
                                />
                            </div>
                        </div>
                    </div>                
                    {isValidForm && formErrorMessage ? 
                        <div className="mt-2 mb-2">
                            <span className="text-red-600">{formErrorMessage}</span> 
                        </div>
                        : null}
                        
                    <Button 
                        aria-label="Create Lot"
                        type="submit"
                        className="text-lg bg-blue-950 hover:bg-blue-500 transition-all"
                        disabled={isValidForm}
                    >
                        Create Lot
                    </Button>
                </form>
                <Dialog>
                    <DialogTrigger className="hidden" id="lotCreatedDialog"></DialogTrigger>
                        <DialogContent>
                            <DialogHeader>
                                <DialogTitle>Lot Created!</DialogTitle>
                                <DialogDescription>
                                    A new parking lot has been created for your group.
                                </DialogDescription>
                            </DialogHeader>
                            <DialogFooter>
                                <ButtonGroup>
                                    <Button size="sm" className="bg-blue-950 text-white hover:bg-blue-500">
                                        <DialogClose>Close</DialogClose>
                                    </Button>
                                    <ButtonGroupSeparator/>
                                    <Button size="sm" className="bg-blue-950 text-white hover:bg-blue-500" onClick={routeHome}>Home</Button>
                                </ButtonGroup>
                            </DialogFooter>
                    </DialogContent>
                </Dialog>
            </div>
            <Footer/>
        </>
    );
}

export default function CreateLotPage() {
    return (
        <Suspense>
            <CreateLotForm/>
        </Suspense>
    )
}