import axios, { AxiosInstance } from 'axios';
import { createToken } from './loginClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = { withCredentials: true };

describe('createToken', () => {
  const validToken = {
    idToken: 'foo',
  };

  beforeEach(() => {
    axiosMock.post.mockResolvedValue({ data: validToken });
  });

  it('should send all stuff to backend', async () => {
    const given = {
      email: 'foo@bar.com',
      password: 'password',
    };

    const actual = createToken(given);
    const expected = validToken;

    await expect(actual).resolves.toBe(expected);
    await expect(axiosMock.post).toHaveBeenCalledWith('backend/token', given, AXIOS_CONFIG);
  });
});
