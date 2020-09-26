export interface User {
  name: string;
  email: string;
  password: string;
  emailIsVerified: boolean;
}

export function register(
  _email: string,
  _password: string,
  _name: string
): Promise<User> {
  return Promise.reject(new Error());
}

export function confirm(_id: string): Promise<User> {
  return Promise.reject(new Error());
}
