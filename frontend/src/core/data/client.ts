import { logger } from '../../logger';

export const baseUrl = process.env.REACT_APP_BACKEND;
export const config = { withCredentials: true };
const accessToken = localStorage.getItem('accessToken');
export const withTokenConfig = {
  withCredentials: true,
  headers: { Authorization: `Bearer ${accessToken}` }
};

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
