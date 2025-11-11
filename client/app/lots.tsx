import { Lot } from "@/types/lot";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
} from "@/components/ui/card"
import Image from 'next/image';
import Map from "./map";

interface ResultProps {
  results: Lot[] | null;
};

interface LotItemProps {
    lot: Lot
}

type LotLevel = {
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
    const levelColors: LotLevel[] = [
        {
            text: "Low",
            color: "bg-[#66BB6A]"
        },
        {
            text: "Normal",
            color: "bg-[#BDBDBD]"
        },
        {
            text: "High",
            color: "bg-[#E57373]"
        },
    ];

    let percentage = (volume / capacity) * 100.00;
    if (percentage < 33.00) {
        return levelColors[0]
    } else if (percentage > 33.00 && percentage < 66.00) {
        return levelColors[1]
    } else {
        return levelColors[2]
    }
}

export function LotItem({ lot }: LotItemProps) {
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

export function LotList({ results }: ResultProps) {
    if (!results) return;

    return (
        <div className="flex max-w-300 justify-center gap-4 flex-wrap">
            {results.map((lot: Lot, index: number) => <LotItem key={index} lot={lot} />)}
        </div>
    );
}