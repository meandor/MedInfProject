import axios from 'axios';
import { baseUrl, withTokenConfig, errorLogging } from '../../core/data/client';

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
    .get(`${baseUrl}menstruation/prediction`, withTokenConfig)
    .then(({ data }) => data)
    .catch(errorLogging);
}
