import axios from 'axios';
import { logger } from '../../logger';

export interface TokenDTO {
  idToken: string;
}

export interface CreateTokenDTO {
  email: string;
  password: string;
}

const baseUrl = process.env.REACT_APP_BACKEND;
const config = { withCredentials: true };

export function errorLogging(error: any): any {
  if (error.response) {
    logger.error(
      `Request failed with status: ${
        error.response.status
      }, body: ${JSON.stringify(error.response.data)}`
    );
  } else if (error.request) {
    logger.error(JSON.stringify(error.request));
  } else {
    logger.error(error.message);
  }
  logger.debug(error.config);
  return Promise.reject(error);
}

export function createToken(dto: CreateTokenDTO): Promise<TokenDTO> {
  return axios
    .post(`${baseUrl}token`, dto, config)
    .then(({ data }) => data)
    .catch(errorLogging);
}
