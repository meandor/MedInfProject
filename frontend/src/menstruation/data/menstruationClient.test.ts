import axios, { AxiosInstance } from 'axios';
import { postMenstruation } from './menstruationClient';

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

    const actual = postMenstruation(menstruationDTO);
    const expected = createdMenstruation;

    await expect(actual).resolves.toBe(expected);
    await expect(axiosMock.post).toHaveBeenCalledWith(
      'backend/menstruation',
      menstruationDTO,
      AXIOS_CONFIG
    );
  });
});
