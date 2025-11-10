import { Lot } from "@/types/lot";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
} from "@/components/ui/card"

type ResultProps = {
  results: Lot[] | null;
};

type LotItemProps = {
    lot: Lot
}

type LotLevel = {
    text: string,
    color: string
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
    console.log(color);
    return (
        <Card>
            <CardHeader>
                <div>
                    <p>{lot.name}</p>
                    <p className={color}>{text}</p>
                    <p>{lot.currentVolume} out of {lot.capacity}</p>
                </div>
            </CardHeader>
            <CardContent>{lot.street}, {lot.city}, {lot.state}, {lot.zip}</CardContent>
            <CardFooter>Put some links down here</CardFooter>
        </Card>
    );
}

export function LotList({ results }: ResultProps) {
    if (!results) return;

    return (
        <div>
            {results.map((lot: Lot, index: number) => <LotItem key={index} lot={lot} />)}
        </div>
    );
}