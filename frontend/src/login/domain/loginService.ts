import { verify } from 'jsonwebtoken';
import { createToken } from '../data/loginClient';
import { logger } from '../../logger';

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
  const idTokenSecret: string = process.env.REACT_APP_ID_TOKEN_SECRET || '';
  return createToken({ email, password }).then((token) => {
    try {
      return verify(token.idToken, idTokenSecret) as IDToken;
    } catch (error) {
      logger.error('Error verifying idToken', error);
      throw new Error('Was not able to login. Please try again.');
    }
  });
}
