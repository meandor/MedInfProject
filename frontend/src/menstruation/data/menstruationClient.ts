import axios from 'axios';
import { baseUrl, errorLogging, withTokenConfig } from '../../core/data/client';

export interface MenstruationDTO {
  start: string;
  end: string;
}

export function postMenstruation(
  menstruationDTO: MenstruationDTO
): Promise<MenstruationDTO> {
  return axios
    .post(`${baseUrl}menstruation`, menstruationDTO, withTokenConfig)
    .then(({ data }) => data)
    .catch(errorLogging);
}
