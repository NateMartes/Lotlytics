"use client"
import { ChangeEvent, useState, useEffect, useRef, FormEvent } from "react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import MapComponent from "./map"
import { Address } from "@/types/address"

class NotANumberError extends Error {
    constructor(message: string) {
        super(message);
    }
}

export default function CreateLotPage() {
    const [name, setName] = useState<string>("");
    const [capacity, setCapacity] = useState<number>(0);
    const [volume, setVolume] = useState<number>(0);
    const [street, setStreet] = useState<string>("");
    const [city, setCity] = useState<string>("");
    const [state, setState] = useState<string>("");
    const [zip, setZip] = useState<string>("");
    const [address, setAddress] = useState<Address | null>(null);
    const [formErrorMessage, setFormErrorMessage] = useState<string | null>(null);
    const [isValidVolume, setIsValidVolume] = useState<boolean>(true);
    const [isValidCapacity, setIsValidCapacity] = useState<boolean>(true);    
    const isValidForm = !isValidVolume || !isValidCapacity;

    useEffect(() => {
    }, []);

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
        setAddress(address);
        setStreet(address.street);
        setCity(address.city);
        setState(address.state);
        setZip(address.zip);
    }

    const handleCreateLotSubmit = () => {
        console.log({
            name,
            capacity,
            volume,
            street,
            city,
            state,
            zip
        });
    };

    return (
        <form className="p-4" onKeyDown={(e) => e.key == "Enter" && e.preventDefault()}>
            <h1 className="text-2xl font-bold mb-4">Define a New Parking Lot</h1>
            <div className="flex flex-col">
                <div className="flex flex-col max-w-lg gap-4 mt-5 mb-5">
                    <Input 
                        required 
                        placeholder="Name" 
                        type="text" 
                        onChange={(event: ChangeEvent<HTMLInputElement>) => setName(event.target.value)}
                    />
                    <Input 
                        required 
                        placeholder="Current Volume" 
                        type="text" 
                        onChange={(event: ChangeEvent<HTMLInputElement>) => validateVolumeInput(event.target.value)}
                    />
                    <Input 
                        required 
                        placeholder="Max Capacity" 
                        type="text" 
                        onChange={(event: ChangeEvent<HTMLInputElement>) => validateCapacityInput(event.target.value)}
                    />
                </div>
                <div className="flex flex-col max-w-lg gap-4 mt-5 mb-5">
                    <MapComponent onAddress={handleNewAddress}/>                        
                    <div className="grid grid-cols-2 gap-2">
                        <Input 
                            placeholder="Street" 
                            type="text"
                            value={street}
                            onChange={(event: ChangeEvent<HTMLInputElement>) => setStreet(event.target.value)}
                            readOnly
                        />
                        <Input 
                            placeholder="City" 
                            type="text" 
                            value={city}
                            onChange={(event: ChangeEvent<HTMLInputElement>) => setCity(event.target.value)}
                            readOnly
                        />
                        <Input 
                            placeholder="State" 
                            type="text" 
                            value={state}
                            onChange={(event: ChangeEvent<HTMLInputElement>) => setState(event.target.value)}
                            readOnly
                        />
                        <Input 
                            placeholder="ZIP" 
                            type="text" 
                            value={zip}
                            onChange={(event: ChangeEvent<HTMLInputElement>) => setZip(event.target.value)}
                            readOnly
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
                onSubmit={handleCreateLotSubmit}
                className="text-lg bg-blue-900 hover:bg-blue-500 transition-all"
                disabled={isValidForm}
            >
                Create Lot
            </Button>
        </form>
    );
}