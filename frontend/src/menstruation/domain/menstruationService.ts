import { MenstruationDTO, postMenstruation } from '../data/menstruationClient';
import { authenticatedUser } from '../../auth/domain/loginService';

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

function toMenstruationDTO(menstruation: Menstruation): MenstruationDTO {
  return {
    start: menstruation.start.toISOString(),
    end: menstruation.end.toISOString(),
  };
}

export function createMenstruation(
  menstruation: Menstruation
): Promise<Menstruation> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return postMenstruation(toMenstruationDTO(menstruation)).then(toMenstruation);
}
