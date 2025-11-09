import type { Metadata } from "next";
import { Navigation } from "./nav";
import "./globals.css";

export const metadata: Metadata = {
  title: "Lotlytics | Make your Trips Easier",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <Navigation/>
        {children}
      </body>
    </html>
  );
}
