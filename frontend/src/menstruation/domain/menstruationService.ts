import { get, MenstruationDTO, post } from '../data/menstruationClient';
import { authenticatedUser } from '../../user/domain/loginService';

export interface Menstruation {
  start: Date;
  end: Date;
}

function toMenstruation(dto: MenstruationDTO): Menstruation {
  return {
    start: new Date(dto.start),
    end: new Date(dto.end),
  };
}

function toDateString(date: Date): string {
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
}

function toMenstruationDTO(menstruation: Menstruation): MenstruationDTO {
  return {
    start: toDateString(menstruation.start),
    end: toDateString(menstruation.end),
  };
}

export function createMenstruation(
  menstruation: Menstruation
): Promise<Menstruation> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return post(toMenstruationDTO(menstruation)).then(toMenstruation);
}

export function find(): Promise<Menstruation[]> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return get().then((menstruation) => menstruation.map(toMenstruation));
}
