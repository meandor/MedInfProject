import axios, { AxiosInstance } from 'axios';
import { createUser, UserDTO } from './registerClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = { withCredentials: true };

describe('registerClient', () => {
  const createdUser: UserDTO = {
    email: 'foo@bar.com',
    password: 'password',
    name: 'foo bar',
    isVerified: false,
  };

  beforeEach(() => {
    axiosMock.post.mockResolvedValue({ data: createdUser });
  });

  it('createUser should return created user', async () => {
    const given: UserDTO = {
      email: 'foo@bar.com',
      password: 'password',
      name: 'foo bar',
      isVerified: false,
    };

    const actual = createUser(given);
    const expected = createdUser;

    await expect(actual).resolves.toBe(expected);
    await expect(axiosMock.post).toHaveBeenCalledWith(
      'backend/user',
      given,
      AXIOS_CONFIG
    );
  });
});
