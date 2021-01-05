import axios, { AxiosInstance } from 'axios';
import { getPrediction } from './predictionClient';

jest.mock('axios');

const axiosMock: jest.Mocked<AxiosInstance> = axios as any;
const AXIOS_CONFIG = {
  withCredentials: true,
  headers: { Authorization: `Bearer null` },
};

describe('getPrediction', () => {
  const prediction = {
    ovulation: {
      startDate: new Date(),
      isActive: false,
    },
    menstruation: {
      startDate: new Date(),
      isActive: true,
      duration: 13,
    },
  };

  beforeEach(() => {
    axiosMock.get.mockResolvedValue({ data: prediction });
  });

  it('should send all stuff to backend', async () => {
    const actual = getPrediction();
    const expected = prediction;

    await expect(actual).resolves.toBe(expected);
    await expect(axiosMock.get).toHaveBeenCalledWith(
      'backend/menstruation/prediction',
      AXIOS_CONFIG
    );
  });
});
