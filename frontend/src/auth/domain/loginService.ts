import { verify } from 'jsonwebtoken';
import { createToken } from '../data/loginClient';
import { logger } from '../../logger';

const ID_TOKEN_KEY = 'idToken';

export interface IDToken {
  name: string;
  email: string;
  email_verified: boolean;
}

export function isAuthenticated(): boolean {
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
  if (!isAuthenticated()) {
    return undefined;
  }

  const rawIdToken = localStorage.getItem(ID_TOKEN_KEY) || '';
  const idTokenSecret: string = process.env.REACT_APP_ID_TOKEN_SECRET || '';
  try {
    return <IDToken>verify(rawIdToken, idTokenSecret);
  } catch (error) {
    logger.error(error);
    return undefined;
  }
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
      return decodedIdToken;
    } catch (error) {
      logger.error('Error verifying idToken', error);
      throw new Error('Was not able to login. Please try again.');
    }
  });
}
