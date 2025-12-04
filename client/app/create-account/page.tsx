"use client"

import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Spinner } from "@/components/ui/spinner"
import { FormEvent, useEffect, useState } from "react"
import { useRouter } from 'next/navigation';
import { useAuth } from "@/context/AuthContext"
import { Navigation } from "@/components/nav"
import { Footer } from "@/components/footer"

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [loggingIn, setLoggingIn] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const router = useRouter();
  const { isAuthenticated, isLoading, refreshUser } = useAuth();

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      router.push("/admin/dashboard");
    }
  }, [isLoading, isAuthenticated, router]);

  const loginNewUser = async () => {
    
    const url = "https://lotlytics-api.nathanielmartes.com/api/v1/user/login";
    
    setLoggingIn(true);
    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, password }),
    })
      .then(async (res: Response) => {
        if (!res.ok) {
          throw new Error(`Login failed. Status: ${res.status}`);
        } else {
          await refreshUser();
          router.push("/admin/dashboard");
        }
      })
      .catch((error: Error) => {
        console.error("Login error:", error);
        setErrorMessage(error.message);
        router.push("/admin");
      })
      .finally(() => {
        setLoggingIn(false);
      });
  }

  const handleLoginSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (username == "" || password == "") {
        setErrorMessage("Please fill in the form.");
        return;
    }

    if (username.length < 6) {
        setErrorMessage("Username must be at least 6 characters.");
        return;
    }

    setLoading(true);
    setErrorMessage(null);

    const url = "https://lotlytics-api.nathanielmartes.com/api/v1/user";
    console.log(JSON.stringify({ username, email, password }))
    fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ username, email, password }),
    })
      .then(async (res: Response) => {
        if (!res.ok) {
          try {
            let resBody = await res.json();
            setErrorMessage(resBody.error);
          } catch {
            throw new Error(`Failed to Create Account: Server Error ${res.status}`);
          }
        } else {
          await loginNewUser();
        }
      })
      .catch((error: Error) => {
        console.error("Error:", error);
        setErrorMessage(error.message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return (
    <>
      <Navigation/>
      <div className="flex flex-col place-items-center mt-20 text-2xl lg:text-3xl gap-4 p-10 md:p-0">
        <p className="text-center">Create an Account</p>
        
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
                disabled={loading || loggingIn}
              />
            </div>

            <div className="flex flex-col gap-2">
              <label htmlFor="email" className="text-sm font-medium">
                Email
              </label>
              <Input
                id="email"
                type="email"
                value={email}
                placeholder="Enter your email"
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={loading || loggingIn}
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
                disabled={loading || loggingIn}
              />
            </div>

            <Button 
              className="bg-blue-950 hover:bg-blue-500 mt-2" 
              disabled={loading || loggingIn}
              type="submit"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <Spinner className="size-4" />
                  Creating Account...
                </span>
              ) : loggingIn ? 
                <span className="flex items-center gap-2">
                  <Spinner className="size-4" />
                  Logging in...
                </span> : ('Create Account') 
              }
            </Button>
          </form>
        </Card>

          <div className="p-4 mt-4 text-center text-base">
            Already have an account? <a className="hover:underline" href="/admin">Login</a>
          </div>

        {errorMessage ? (
          <div className="p-4 mt-4 text-center text-red-500 text-base">
            {errorMessage}
          </div>
        ) : null}
      </div>
      <Footer/>
    </>
  );
}