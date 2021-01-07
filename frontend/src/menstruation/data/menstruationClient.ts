import axios from 'axios';
import { baseUrl, errorLogging, withTokenConfig } from '../../core/data/client';

export interface MenstruationDTO {
  start: string;
  end: string;
}

export function post(
  menstruationDTO: MenstruationDTO
): Promise<MenstruationDTO> {
  return axios
    .post(`${baseUrl}menstruation`, menstruationDTO, withTokenConfig)
    .then(({ data }) => data)
    .catch(errorLogging);
}

export function get(): Promise<MenstruationDTO[]> {
  return axios
    .get(`${baseUrl}menstruation`, withTokenConfig)
    .then(({ data }) => data)
    .catch(errorLogging);
}
