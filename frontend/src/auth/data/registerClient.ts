import axios from 'axios';
import { baseUrl, config, errorLogging } from '../../core/data/client';

export interface UserDTO {
  email: string;
  password: string;
  name: string | undefined;
  isVerified: boolean;
}

export interface ConfirmationDTO {
  id: string;
}

export function confirmUser(dto: ConfirmationDTO): Promise<UserDTO> {
  return axios
    .post(`${baseUrl}confirmation`, dto, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}

export function createUser(dto: UserDTO): Promise<UserDTO> {
  return axios
    .post(`${baseUrl}user`, dto, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}
