import axios, { AxiosInstance } from 'axios';
import {
  anonymizeData,
  deleteUserData,
  deleteUserAccount,
} from './deleteClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = {
  withCredentials: true,
  headers: { Authorization: `Bearer null` },
};

describe('deleteClient', () => {
  it('should anonymizeData', async () => {
    axiosMock.delete.mockResolvedValue({ data: {} });

    const actual = anonymizeData();

    await expect(actual).resolves.toBeUndefined();
    await expect(axiosMock.delete).toHaveBeenCalledWith(
      'backend/user/identifiable-data',
      AXIOS_CONFIG
    );
  });

  it('should delete user data', async () => {
    axiosMock.delete.mockResolvedValue({ data: {} });

    const actual = deleteUserData();

    await expect(actual).resolves.toBeUndefined();
    await expect(axiosMock.delete).toHaveBeenCalledWith(
      'backend/user/data',
      AXIOS_CONFIG
    );
  });

  it('should delete user', async () => {
    axiosMock.delete.mockResolvedValue({ data: {} });

    const actual = deleteUserAccount();

    await expect(actual).resolves.toBeUndefined();
    await expect(axiosMock.delete).toHaveBeenCalledWith(
      'backend/user',
      AXIOS_CONFIG
    );
  });
});
