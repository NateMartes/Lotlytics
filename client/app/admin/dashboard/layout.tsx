import { AuthProvider } from "@/context/AuthContext";

export default function CreateLotPageLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
        </*AuthProvider*/>
            {children}
        <//*AuthProvider*/>
  );
}

