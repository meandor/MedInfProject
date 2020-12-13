import axios from 'axios';
import { baseUrl, config, errorLogging } from '../../core/data/client';

export interface PredictionDTO {
  ovulation: {
    startDate: string;
    isActive: boolean;
  };
  period: {
    startDate: string;
    isActive: boolean;
    duration: number;
  };
}

export function getPrediction(): Promise<PredictionDTO> {
  return axios
    .get(`${baseUrl}menstruation/prediction`, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}
