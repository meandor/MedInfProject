import axios from 'axios';
import { baseUrl, config, errorLogging } from '../../core/data/client';

export interface TokenDTO {
  idToken: string;
  accessToken: string;
}

export interface CreateTokenDTO {
  email: string;
  password: string;
}

export function createToken(dto: CreateTokenDTO): Promise<TokenDTO> {
  return axios
    .post(`${baseUrl}token`, dto, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}
