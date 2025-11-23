export class NotLoggedInError extends Error {
  constructor(message: string,) {
    super(message);
  }
}