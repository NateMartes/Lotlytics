import type { Metadata } from "next";
import { Navigation } from "./nav";
import { Footer } from "./footer";
import "./globals.css";

export const metadata: Metadata = {
  title: "Lotlytics | Make your Trips Easier",
  icons: {
    icon: "/favicon.ico"
  }
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
        <Footer />
      </body>
    </html>
  );
}
