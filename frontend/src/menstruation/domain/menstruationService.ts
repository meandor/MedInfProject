import { get, MenstruationDTO, post } from '../data/menstruationClient';
import { authenticatedUser } from '../../user/domain/loginService';

export interface Menstruation {
  start: Date;
  end: Date;
}

function toLocalDate(date: string): Date {
  const dateWithTimezone = new Date(date);
  const timezoneOffset = dateWithTimezone.getTimezoneOffset();
  return new Date(dateWithTimezone.getTime() + timezoneOffset * 60000);
}

function toMenstruation(dto: MenstruationDTO): Menstruation {
  return {
    start: toLocalDate(dto.start),
    end: toLocalDate(dto.end),
  };
}

function toDateString(date: Date): string {
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
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
