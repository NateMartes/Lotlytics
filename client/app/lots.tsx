import { Lot } from "@/types/lot";

type ResultProps = {
  results: Lot[] | null;
};

export function LotList({ results }: ResultProps) {
    if (!results) return;

    return (
        <ul>
            {results.map((lot: Lot, index: number) => <li key={index}>Lot: {lot.id}</li>)}
        </ul>
    );
}