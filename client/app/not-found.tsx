import { CloudAlert } from "lucide-react";
import Link from "next/link";
import "@/app/globals.css";

export default function NotFoundPage() {
  return (
    <div className="flex flex-col gap-4 p-10">
      <div className="flex flex-wrap gap-2">
        <CloudAlert size={40} />
        <h1 className="text-left text-xl lg:text-3xl">404 Error</h1>
      </div>
      <p className="text-left text-xl lg:text-3xl">The page you are looking for does not exist.</p>
      <Link className="text-left text-xl lg:text-3xl hover:underline mt-10" href="/">Return Home</Link>
    </div>
  );
}