import { authenticatedUser } from '../../auth/domain/loginService';
import { getPrediction, PredictionDTO } from '../data/predictionClient';

export enum Event {
  MENSTRUATION = 'Period',
  OVULATION = 'Ovulation',
}

export interface Prediction {
  event: Event;
  isUpcoming: boolean;
  days: number;
}

function toPrediction(predictionDTO: PredictionDTO): Prediction {
  const ovulationStartDate = new Date(predictionDTO.ovulation.startDate);
  const menstruationStartDate = new Date(predictionDTO.menstruation.startDate);
  if (ovulationStartDate.getTime() < menstruationStartDate.getTime()) {
    return {
      event: Event.OVULATION,
      isUpcoming: !predictionDTO.ovulation.isActive,
      days: 1,
    };
  }

  return {
    event: Event.MENSTRUATION,
    isUpcoming: !predictionDTO.menstruation.isActive,
    days: predictionDTO.menstruation.duration,
  };
}

export function predict(): Promise<Prediction> {
  const idToken = authenticatedUser();
  if (idToken === undefined) {
    return Promise.reject(new Error('User not found'));
  }

  return getPrediction().then(toPrediction);
}
