export function isAuthenticated(): boolean {
  return false;
}

export interface IDToken {
  name: string;
  email: string;
  email_verified: boolean;
}

export function authenticate(
  email: string,
  password: string
): Promise<IDToken> {
  return Promise.reject(new Error('login error'));
}
