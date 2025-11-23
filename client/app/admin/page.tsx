"use client"

import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Spinner } from "@/components/ui/spinner"
import { FormEvent, useState } from "react"
import { useRouter } from 'next/navigation';

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const router = useRouter();

  const handleLoginSubmit = (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setErrorMessage(null);

    const url = "http://localhost:6600/api/v1/user/login";
    
    fetch(url, {
      credentials: "include",
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    })
      .then((res: Response) => {
        if (!res.ok) {
          throw new Error(`Login failed. Status: ${res.status}`);
        } else {
         router.push("/admin/create-lot");
        }
      })
      .catch((error: Error) => {
        console.error("Login error:", error);
        setErrorMessage("Invalid username or password. Please try again.");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <div className="flex flex-col place-items-center mt-20 text-2xl lg:text-3xl gap-4">
      <p className="text-center">Login</p>
      
      <Card className="md:min-w-96 w-full max-w-md">
        <form className="flex flex-col p-6 gap-4" onSubmit={handleLoginSubmit}>
          <div className="flex flex-col gap-2">
            <label htmlFor="username" className="text-sm font-medium">
              Username
            </label>
            <Input
              id="username"
              type="text"
              value={username}
              placeholder="Enter your username"
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="flex flex-col gap-2">
            <label htmlFor="password" className="text-sm font-medium">
              Password
            </label>
            <Input
              id="password"
              type="password"
              value={password}
              placeholder="Enter your password"
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <Button 
            className="bg-blue-900 hover:bg-blue-400 mt-2" 
            disabled={loading}
            type="submit"
          >
            {loading ? (
              <span className="flex items-center gap-2">
                <Spinner className="size-4" />
                Logging in...
              </span>
            ) : (
              'Login'
            )}
          </Button>
        </form>
      </Card>

      {errorMessage ? (
        <div className="p-4 mt-4 text-center text-red-500 text-base">
          {errorMessage}
        </div>
      ) : null}
    </div>
  );
}