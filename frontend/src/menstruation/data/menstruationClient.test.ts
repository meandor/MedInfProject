import axios, { AxiosInstance } from 'axios';
import { get, post } from './menstruationClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = {
  withCredentials: true,
  headers: { Authorization: `Bearer null` },
};

describe('menstruationClient', () => {
  it('should send menstruation to backend', async () => {
    const createdMenstruation = { start: '2020-01-01', end: '2020-01-01' };
    axiosMock.post.mockResolvedValue({ data: createdMenstruation });
    const menstruationDTO = {
      start: '2020-01-01',
      end: '2020-01-01',
    };

    const actual = post(menstruationDTO);
    const expected = createdMenstruation;

    await expect(actual).resolves.toBe(expected);
    await expect(axiosMock.post).toHaveBeenCalledWith(
      'backend/menstruation',
      menstruationDTO,
      AXIOS_CONFIG
    );
  });

  it('should get menstruation from backend', async () => {
    const menstruationDTO = {
      start: '2020-01-01',
      end: '2020-01-01',
    };
    axiosMock.get.mockResolvedValue({ data: [menstruationDTO] });

    const actual = get();
    const expected = [menstruationDTO];

    await expect(actual).resolves.toStrictEqual(expected);
    await expect(axiosMock.get).toHaveBeenCalledWith(
      'backend/menstruation',
      AXIOS_CONFIG
    );
  });
});
