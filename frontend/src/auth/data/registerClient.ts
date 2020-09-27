import axios from 'axios';
import { baseUrl, config, errorLogging } from '../../core/data/client';

export interface UserDTO {
  email: string;
  password: string;
  name: string | undefined;
  isVerified: boolean;
}

export function createUser(dto: UserDTO): Promise<UserDTO> {
  return axios
    .post(`${baseUrl}user`, dto, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}
