import axios from 'axios';
import { baseUrl, errorLogging, withTokenConfig } from '../../core/data/client';

export function anonymizeData(): Promise<void> {
  return axios
    .delete(`${baseUrl}user/identifiable-data`, withTokenConfig)
    .then((_data) => undefined)
    .catch(errorLogging);
}
export function deleteUserData(): Promise<void> {
  return axios
    .delete(`${baseUrl}user/data`, withTokenConfig)
    .then((_data) => undefined)
    .catch(errorLogging);
}
export function deleteUserAccount(): Promise<void> {
  return axios
    .delete(`${baseUrl}user`, withTokenConfig)
    .then((_data) => undefined)
    .catch(errorLogging);
}
