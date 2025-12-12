import { User } from "@/types/auth";
import { API_URL } from "@/types/url";

/**
 * The createUser function creates a new user on the backend.
 *
 * @param username The users username.
 * @param email The users email.
 * @param password The users password.
 * @param callback A function to call when the user is successfully created.
 * @param errorCallback A function to call when an error occurs while creating the user.
 */
export function createUser(
  username: string,
  email: string,
  password: string,
  callback: () => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_URL + "/user";
  fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, email, password }),
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        throw new Error(`Failed to Create Account: Server Error ${res.status}`);
      } else {
        callback();
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

/**
 * The loginUser function logs a user into the backend API.
 *
 * @param username The users username.
 * @param password The users password.
 * @param callback A function to call when the user is successfully logged in.
 * @param errorCallback A function to call when an error occurs while logging the user in.
 */
export function loginUser(
  username: string,
  password: string,
  callback: () => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_URL + "/user/login";
  fetch(url, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        throw new Error(`Login failed. Status: ${res.status}`);
      } else {
        callback();
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

/**
 * The isLoggedIn function checks if the user is currently logged in by using
 * HTTP_ONLY cookies
 *
 * @param callback A function to call when the user is checked for being logged in
 * @param errorCallback A function to call when an error occurs while checking the user.
 */
export function isLoggedIn(
  callback: (u: User | null) => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_URL + "/user/me";

  fetch(url, { credentials: "include" })
    .then(async (response: Response) => {
      if (response.ok) {
        const user: User = await response.json();
        callback(user);
      } else if (response.status > 500) {
        throw new Error("Failed to determine if user is logged in");
      } else {
        callback(null);
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}

/**
 * The logoutUser function logs the user out my invalidating the token on the backend.
 *
 * @param callback A function to call when the user is checked for being logged in
 * @param errorCallback A function to call when an error occurs while checking the user.
 */
export function logoutUser(
  callback: () => void,
  errorCallback: (e: Error) => void,
) {
  const url = API_URL + "/user/logout";
  fetch(url, {
    credentials: "include",
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then(async (res: Response) => {
      if (!res.ok) {
        throw new Error(`Logout failed. Status: ${res.status}`);
      } else {
        callback();
      }
    })
    .catch((error: Error) => {
      errorCallback(error);
    });
}
