import { ForwardedRef, forwardRef, useImperativeHandle, useMemo, useState } from "react";
import { Lot } from "@/types/lot";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card"
import Image from 'next/image';
import { Map } from "@/components/google-map";
import { ArrowUpRight } from "lucide-react";

type LotOccupancyLevel = "low" | "medium" | "high";

interface LotItemProps {
    lot: Lot
}

type LotLevel = {
    level: LotOccupancyLevel,
    text: string,
    color: string
}

function getLotMapLinks() {
    return (
        <div className="flex gap-4">
            <Image width="48" height="48" className="cursor-pointer" src="/apple.avif" alt="Apple Maps"/>
            <Image width="48" height="48" className="cursor-pointer" src="/google.avif" alt="Google Maps"/>
            <Image width="48" height="48" className="cursor-pointer" src="/waze.avif" alt="Waze"/>
        </div>
    )
}
function getLotLevel(volume: number, capacity: number) {
    const levelColors: LotLevel[] = [{
            level: "low",
            text: "Low",
            color: "bg-[#66BB6A]"
        },{
            level: "medium",
            text: "Medium",
            color: "bg-[#BDBDBD]"
        },{
            level: "high",
            text: "High",
            color: "bg-[#E57373]"
        }];

    let percentage = capacity > 0 ? (volume / capacity) * 100.00 : 0;
    if (percentage <= 33.00) {
        return levelColors[0]
    } else if (percentage > 33.00 && percentage < 66.00) {
        return levelColors[1]
    } else {
        return levelColors[2]
    }
}

export function LotItem({ lot }: LotItemProps) {
    console.log(lot.street);
    const { text, color } = getLotLevel(lot.currentVolume, lot.capacity);
    return (
        <Card className="min-w-100">
            <CardHeader>
                <div>
                    <div className="flex gap-2 place-items-center">
                        <p className="max-w-50">{lot.name}</p>
                        <span className={`inline-block px-2 py-1 rounded-full text-sm font-medium ${color}`}>
                            {text}
                        </span>
                        <a className="justify-self-end-safe">
                            <ArrowUpRight />
                        </a>
                    </div>
                    <Map street={lot.street} city={lot.city} state={lot.state} zip={lot.zip}/>
                    <p className="text-sm text-muted-foreground">{lot.currentVolume} / {lot.capacity}</p>
                </div>
            </CardHeader>
            <CardContent><p className="max-w-100 text-sm text-wrap">{lot.street}, {lot.city}, {lot.state}, {lot.zip}</p></CardContent>
            <CardFooter>{getLotMapLinks()}</CardFooter>
        </Card>
    );
}

type LotFilterOption = "all" | LotOccupancyLevel;

export type LotListHandle = {
    setLots: (lots: Lot[]) => void;
    clearLots: () => void;
    setFilter: (filter: LotFilterOption) => void;
};

interface LotListProps {
    hasSearched: boolean;
}

const filterOptions: { label: string; value: LotFilterOption; color: string }[] = [
    { label: "All", value: "all", color: "grey"},
    { label: "Low", value: "low", color: "bg-[#66BB6A]"},
    { label: "Medium", value: "medium", color: "bg-[#BDBDBD]"},
    { label: "High", value: "high", color: "bg-[#E57373]"}
];

function LotListComponent({ hasSearched }: LotListProps, ref: ForwardedRef<LotListHandle>) {
    const [lots, setLots] = useState<Lot[]>([]);
    const [selectedFilter, setSelectedFilter] = useState<LotFilterOption>("all");

    useImperativeHandle(ref, () => ({
        setLots: (incomingLots: Lot[]) => {
            setLots(incomingLots ?? []);
        },
        clearLots: () => {
            setLots([]);
        },
        setFilter: (filter: LotFilterOption) => {
            setSelectedFilter(filter);
        }
    }), []);

    const filteredLots = useMemo(() => {
        if (selectedFilter === "all") {
            return lots;
        }

        return lots.filter((lot: Lot) => {
            const { level } = getLotLevel(lot.currentVolume, lot.capacity);
            return level === selectedFilter;
        });
    }, [lots, selectedFilter]);

    const hasLots = lots.length > 0;
    const hasFilteredLots = filteredLots.length > 0;

    return (
        <div className="flex flex-col max-w-400 mx-auto">
            {hasLots ? (
                <div className="flex flex-wrap items-center justify-center gap-3 mb-6">
                    {filterOptions.map(({ label, value }) => {
                        const isActive = selectedFilter === value;
                        let colorClass = "hover:bg-muted"
                        switch(value) {
                            case "low":
                                colorClass = "hover:bg-[#66BB6A]";
                                break;
                            case "medium":
                                colorClass = "hover:bg-[#BDBDBD]";
                                break;
                            case "high":
                                colorClass = "hover:bg-[#E57373]";
                                break;
                        }
                        return (
                            <button
                                key={value}
                                type="button"
                                onClick={() => setSelectedFilter(value)}
                                className={`rounded-full border px-4 py-2 text-base transition ${
                                    isActive
                                        ? "bg-primary text-primary-foreground border-primary"
                                        : "bg-background text-foreground border-muted " + colorClass
                                }`}
                            >
                                {label}
                            </button>
                        );
                    })}
                </div>
            ) : null}

            {!hasLots && hasSearched ? (
                <p className="text-center text-base text-muted-foreground">No parking lots just yet...</p>
            ) : null}

            {hasLots && !hasFilteredLots ? (
                <p className="text-center text-base text-muted-foreground">No parking lots match this filter.</p>
            ) : null}

            {hasFilteredLots ? (
                <div className="flex justify-center gap-4 flex-wrap">
                    {filteredLots.map((lot: Lot, index: number) => (
                        <LotItem key={`${lot.id}-${index}`} lot={lot} />
                    ))}
                </div>
            ) : null}
        </div>
    );
}

export const LotList = forwardRef<LotListHandle, LotListProps>(LotListComponent);