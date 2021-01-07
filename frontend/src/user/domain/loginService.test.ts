import { verify } from 'jsonwebtoken';
import { authenticate } from './loginService';
import { createToken, TokenDTO } from '../data/loginClient';

jest.mock('../data/loginClient');
jest.mock('jsonwebtoken');

const createTokenMock = createToken as jest.Mock<Promise<TokenDTO>>;
const verifyMock = verify as jest.Mock;

describe('authenticate', () => {
  const validIdToken = {
    name: 'foo bar',
    email: 'foo@bar.com',
    email_verified: true,
  };
  const signedIdToken = 'foo';

  beforeEach(() => {
    createTokenMock.mockResolvedValue({
      idToken: signedIdToken,
      accessToken: signedIdToken,
    });
    verifyMock.mockReturnValue(validIdToken);
  });

  it('should send successful request to backend', async () => {
    const email = 'foo@bar.com';
    const password = 'password';

    const actual = await authenticate(email, password);
    const expected = validIdToken;

    expect(createTokenMock).toHaveBeenCalledWith({ email, password });
    expect(verifyMock).toHaveBeenCalledWith(signedIdToken, 'secret');
    expect(actual).toBe(expected);
  });

  it('should send error when verifying token fails', async () => {
    verifyMock.mockImplementation(() => {
      throw new Error('expected error when verifying token');
    });
    const email = 'foo@bar.com';
    const password = 'password';

    const actual = authenticate(email, password);

    await expect(createTokenMock).toHaveBeenCalledWith({ email, password });
    await expect(verifyMock).toHaveBeenCalledWith(signedIdToken, 'secret');
    await expect(actual).rejects.toBeInstanceOf(Error);
  });
});
