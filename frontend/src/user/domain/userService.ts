import { authenticatedUser } from './loginService';
import {
  anonymizeData,
  deleteUserAccount,
  deleteUserData,
} from '../data/deleteClient';

export function unlinkData(): Promise<void> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return anonymizeData();
}

export function deleteData(): Promise<void> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return deleteUserData();
}

export function deleteAccount(): Promise<void> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return deleteUserAccount();
}
