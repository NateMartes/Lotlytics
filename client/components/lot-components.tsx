import {
  ForwardedRef,
  forwardRef,
  RefObject,
  useImperativeHandle,
  useMemo,
  useState,
} from "react";
import {
  Lot,
  LotListHandle,
  LotLevel,
  LotFilterOption,
  createLot,
} from "@/types/lot";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from "@/components/ui/card";
import Image from "next/image";
import { Map } from "@/components/google-map";
import { ArrowUpRight, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "./ui/button";

const API_BASE_PATH = "http://localhost/api/v1/";

/**
 * The postLot function creates a new lot on the backend.
 *
 * @param name The name of the parking lot.
 * @param capacity The capacity of the parking lot.
 * @param volume The volume of the parking lot.
 * @param street The street the parking lot is on.
 * @param city The city the parking lot is in.
 * @param state The state the parking lot is in.
 * @param zip The zip code of the parking lot.
 * @param callback A function to call when the parking lot has been successfully created.
 * @param errorCallback A function to call when an error occurs creating the parking lot.
 */
export function postLot(
  groupId: string,
  name: string,
  capacity: number,
  volume: number,
  street: string,
  city: string,
  state: string,
  zip: string,
  callback: (response: JSON) => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_BASE_PATH + `lot?groupId=${groupId}`;
  const payload = {
    name: name,
    capacity: capacity,
    volume: volume,
    street: street,
    city: city,
    state: state,
    zip: zip,
  };

  fetch(url, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  })
    .then(async (res: Response) => {
      if (res.ok) {
        const body = await res.json();
        callback(body);
      } else {
        throw new Error(`Failed to create parking lot. status ${res.status}`);
      }
    })
    .catch(errorCallback);
}
/**
 * The getAllLots function gets all lots from the backend.
 * @param lotListRef A possible list to store the lots into.
 * @param callback A function to run once the lots have been gathered.
 * @param errorCallback A function to run when an error occurs.
 */
export function getAllLots(
  lotListRef: RefObject<LotListHandle | null>,
  callback: (l: Lot[]) => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_BASE_PATH + "lot";
  fetch(url)
    .then((res: Response) => {
      if (!res.ok) {
        throw new Error(`Unable to fetch parking lots. Status: ${res.status}`);
      }
      return res.json();
    })
    .then((data: Lot[]) => {
      const lots = data.map((lot) =>
        createLot(
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
          lot.updatedAt,
        ),
      );
      lotListRef?.current?.setLots(lots);
      lotListRef?.current?.setFilter("all");
      callback(lots);
    })
    .catch((error: Error) => {
      errorCallback(error);
    })
}

interface LotItemProps {
  lot: Lot;
}

function getLotMapLinks(lot: Lot) {
  return (
    <div className="flex gap-4">
      <Image
        width="48"
        height="48"
        className="cursor-pointer"
        src="/apple.avif"
        alt="Apple Maps"
      />
      <Image
        width="48"
        height="48"
        className="cursor-pointer"
        src="/google.avif"
        alt="Google Maps"
      />
      <Image
        width="48"
        height="48"
        className="cursor-pointer"
        src="/waze.avif"
        alt="Waze"
      />
    </div>
  );
}

function getLotLevel(volume: number, capacity: number) {
  const levelColors: LotLevel[] = [
    {
      level: "low",
      text: "Low",
      color: "bg-[#66BB6A]",
    },
    {
      level: "medium",
      text: "Medium",
      color: "bg-[#BDBDBD]",
    },
    {
      level: "high",
      text: "High",
      color: "bg-[#E57373]",
    },
  ];

  const percentage = capacity > 0 ? (volume / capacity) * 100.0 : 0;
  if (percentage <= 33.0) {
    return levelColors[0];
  } else if (percentage > 33.0 && percentage < 66.0) {
    return levelColors[1];
  } else {
    return levelColors[2];
  }
}

function fixLotName(lotName: string) {
  if (lotName.length > 20) {
    return lotName.substring(0, 20) + "...";
  } else {
    return lotName;
  }
}

export function LotItem({ lot }: LotItemProps) {
  const { text, color } = getLotLevel(lot.currentVolume, lot.capacity);
  return (
    <Card className="w-full h-full flex flex-col justify-between overflow-hidden">
      <CardHeader>
        <div>
          <div className="flex gap-2 place-items-center justify-between">
            <div className="flex items-center gap-2">
              <p title={lot.name}>{fixLotName(lot.name)}</p>
              <span
                className={`inline-block px-2 py-1 rounded-full text-xs font-medium ${color} text-white`}
              >
                {text}
              </span>
            </div>
            <a href="#" className="text-muted-foreground hover:text-foreground">
              <ArrowUpRight size={20} />
            </a>
          </div>
          <div className="my-2">
            <Map
              street={lot.street}
              city={lot.city}
              state={lot.state}
              zip={lot.zip}
            />
          </div>
          <p className="text-sm text-muted-foreground font-medium">
            {lot.currentVolume} / {lot.capacity} spots taken
          </p>
        </div>
      </CardHeader>
      <CardContent>
        <p className="text-sm text-muted-foreground">
          {lot.street}, {lot.city}, {lot.state}, {lot.zip}
        </p>
      </CardContent>
      <CardFooter>{getLotMapLinks(lot)}</CardFooter>
    </Card>
  );
}

interface LotListProps {
  searching: boolean;
}

const filterOptions: {
  label: string;
  value: LotFilterOption;
  color: string;
}[] = [
  { label: "All", value: "all", color: "grey" },
  { label: "Low", value: "low", color: "bg-[#66BB6A]" },
  { label: "Medium", value: "medium", color: "bg-[#BDBDBD]" },
  { label: "High", value: "high", color: "bg-[#E57373]" },
];

const ITEMS_PER_PAGE = 6;

function LotListComponent(
  { searching }: LotListProps,
  ref: ForwardedRef<LotListHandle>,
) {
  const [lots, setLots] = useState<Lot[]>([]);
  const [selectedFilter, setSelectedFilter] = useState<LotFilterOption>("all");
  const [currentPage, setCurrentPage] = useState<number>(1);

  useImperativeHandle(
    ref,
    () => ({
      setLots: (incomingLots: Lot[]) => {
        setLots(incomingLots ?? []);
        setCurrentPage(1);
      },
      clearLots: () => {
        setLots([]);
        setCurrentPage(1);
      },
      setFilter: (filter: LotFilterOption) => {
        setSelectedFilter(filter);
        setCurrentPage(1);
      },
    }),
    [],
  );

  const filteredLots = useMemo(() => {
    if (selectedFilter === "all") {
      return lots;
    }
    return lots.filter((lot: Lot) => {
      const { level } = getLotLevel(lot.currentVolume, lot.capacity);
      return level === selectedFilter;
    });
  }, [lots, selectedFilter]);

  const totalPages = Math.ceil(filteredLots.length / ITEMS_PER_PAGE);

  const currentDisplayedLots = useMemo(() => {
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const endIndex = startIndex + ITEMS_PER_PAGE;
    return filteredLots.slice(startIndex, endIndex);
  }, [filteredLots, currentPage]);

  const handleFilterClick = (value: LotFilterOption) => {
    setSelectedFilter(value);
    setCurrentPage(1);
  };

  const getPageControls = () => {
    return (
      <>
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-4 mt-4">
            <Button
              variant="outline"
              size="icon"
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
            >
              <ChevronLeft className="h-4 w-4" />
            </Button>

            <span className="text-sm text-muted-foreground">
              Page {currentPage} of {totalPages}
            </span>

            <Button
              variant="outline"
              size="icon"
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalPages))
              }
              disabled={currentPage === totalPages}
            >
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        )}
      </>
    );
  };

  const hasLots = lots.length > 0;
  const hasFilteredLots = filteredLots.length > 0;

  return (
    <div className="flex flex-col items-center w-full space-y-6">
      {hasLots ? (
        <div className="flex flex-wrap items-center justify-center gap-3">
          {filterOptions.map(({ label, value }) => {
            const isActive = selectedFilter === value;
            let colorClass = "hover:bg-muted";
            switch (value) {
              case "low":
                colorClass = "hover:bg-[#66BB6A] hover:text-white";
                break;
              case "medium":
                colorClass = "hover:bg-[#BDBDBD] hover:text-white";
                break;
              case "high":
                colorClass = "hover:bg-[#E57373] hover:text-white";
                break;
            }
            return (
              <Button
                key={value}
                type="button"
                onClick={() => handleFilterClick(value)}
                className={`rounded-full border px-4 py-2 text-base transition ${
                  isActive
                    ? "bg-primary text-primary-foreground border-primary"
                    : "bg-background text-foreground border-muted " + colorClass
                }`}
              >
                {label}
              </Button>
            );
          })}
        </div>
      ) : null}

      {!hasLots && !searching ? (
        <p className="text-center text-base text-muted-foreground">
          No parking lots just yet...
        </p>
      ) : null}

      {hasLots && !hasFilteredLots ? (
        <p className="text-center text-base text-muted-foreground">
          No parking lots match this filter.
        </p>
      ) : null}

      {getPageControls()}
      {hasFilteredLots ? (
        <div className="w-full max-w-6xl flex flex-col gap-6 p-5">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {currentDisplayedLots.map((lot: Lot) => (
              <LotItem key={lot.id} lot={lot} />
            ))}
          </div>
        </div>
      ) : null}
      {getPageControls()}
    </div>
  );
}

export const LotList = forwardRef<LotListHandle, LotListProps>(
  LotListComponent,
);
