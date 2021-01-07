import axios, { AxiosInstance } from 'axios';
import { confirmUser, createUser, UserDTO } from './registerClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = { withCredentials: true };

describe('registerClient', () => {
  describe('createUser', () => {
    const createdUser: UserDTO = {
      email: 'foo@bar.com',
      password: 'password',
      name: 'foo bar',
      isVerified: false,
    };

    beforeEach(() => {
      axiosMock.post.mockResolvedValue({ data: createdUser });
    });

    it('should return created user', async () => {
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

  describe('confirmUser', () => {
    const confirmedUser: UserDTO = {
      email: 'foo@bar.com',
      password: 'password',
      name: 'foo bar',
      isVerified: true,
    };

    beforeEach(() => {
      axiosMock.post.mockResolvedValue({ data: confirmedUser });
    });

    it('should return confirmed user', async () => {
      const given = { id: 'foo' };

      const actual = confirmUser(given);
      const expected = confirmedUser;

      await expect(actual).resolves.toBe(expected);
      await expect(axiosMock.post).toHaveBeenCalledWith(
        'backend/user/confirm',
        given,
        AXIOS_CONFIG
      );
    });
  });
});
