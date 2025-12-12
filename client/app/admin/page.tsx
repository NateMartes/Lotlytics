"use client";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/context/AuthContext";
import { Navigation } from "@/components/nav";
import { Footer } from "@/components/footer";
import { loginUser } from "@/components/user-components";

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const router = useRouter();
  const { isAuthenticated, isLoading, refreshUser } = useAuth();

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      router.push("/admin/dashboard");
    }
  }, [isLoading, isAuthenticated, router]);

  const handleLoginSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (username == "" || password == "") {
      setErrorMessage("Please fill in the form.");
      return;
    }

    setLoading(true);
    setErrorMessage(null);
    loginUser(
      username,
      password,
      async () => {
        setLoading(false);
        await refreshUser();
        router.push("/admin/dashboard");
      },
      () => {
        setErrorMessage("Invalid username or password. Please try again.");
      },
    );
  };

  return (
    <>
      <Navigation />
      <div className="flex flex-col place-items-center mt-20 text-2xl lg:text-3xl gap-4 p-10 md:p-0">
        <p className="text-center">Login</p>

        <Card className="md:min-w-96 w-full max-w-md">
          <form
            className="flex flex-col p-6 gap-4"
            onSubmit={handleLoginSubmit}
          >
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
              className="bg-blue-950 hover:bg-blue-500 mt-2"
              disabled={loading}
              type="submit"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <Spinner className="size-4" />
                  Logging in...
                </span>
              ) : (
                "Login"
              )}
            </Button>
          </form>
        </Card>

        <div className="p-4 mt-4 text-center text-base">
          No account?{" "}
          <a className="hover:underline" href="/create-account">
            Create one!
          </a>
        </div>

        {errorMessage ? (
          <div className="p-4 mt-4 text-center text-red-500 text-base">
            {errorMessage}
          </div>
        ) : null}
      </div>
      <Footer />
    </>
  );
}
