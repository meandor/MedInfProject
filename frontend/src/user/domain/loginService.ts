import { verify } from 'jsonwebtoken';
import { createToken } from '../data/loginClient';
import { logger } from '../../logger';

const ID_TOKEN_KEY = 'idToken';
const ACCESS_TOKEN_KEY = 'accessToken';

export interface IDToken {
  name: string;
  email: string;
  email_verified: boolean;
  sub: string;
}

export function isAuthenticated(): boolean {
  if (process.env.REACT_APP_LOCAL) {
    return true;
  }

  const idToken = localStorage.getItem(ID_TOKEN_KEY) || '';
  if (idToken === '') {
    return false;
  }

  const idTokenSecret: string = process.env.REACT_APP_ID_TOKEN_SECRET || '';
  try {
    verify(idToken, idTokenSecret);
    return true;
  } catch (error) {
    logger.error(error);
    return false;
  }
}

export function authenticatedUser(): IDToken | undefined {
  if (process.env.REACT_APP_LOCAL) {
    return {
      name: "foo bar",
      email: "foo@bar.com",
      email_verified: true,
      sub: "fooooasdasd"
    }
  }
  if (!isAuthenticated()) {
    return undefined;
  }

  const rawIdToken = localStorage.getItem(ID_TOKEN_KEY) || '';
  const idTokenSecret: string = process.env.REACT_APP_ID_TOKEN_SECRET || '';
  try {
    return verify(rawIdToken, idTokenSecret) as IDToken;
  } catch (error) {
    logger.error(error);
    return undefined;
  }
}

export function signOutUser(): void {
  localStorage.removeItem(ID_TOKEN_KEY);
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

export function authenticate(
  email: string,
  password: string
): Promise<IDToken> {
  const idTokenSecret: string = process.env.REACT_APP_ID_TOKEN_SECRET || '';
  return createToken({ email, password }).then((token) => {
    try {
      const decodedIdToken = verify(token.idToken, idTokenSecret) as IDToken;
      localStorage.setItem(ID_TOKEN_KEY, token.idToken);
      localStorage.setItem(ACCESS_TOKEN_KEY, token.accessToken);
      return decodedIdToken;
    } catch (error) {
      logger.error('Error verifying idToken', error);
      throw new Error('Was not able to login. Please try again.');
    }
  });
}
