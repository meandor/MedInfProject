export enum Event {
  PERIOD = 'Period',
  OVULATION = 'Ovulation',
}

export interface Prediction {
  event: Event;
  isUpcoming: boolean;
  days: number;
}

export function predict(): Promise<Prediction> {
  // function daysUntilOvulation(prediction: PredictionDTO): JSX.Element {
  //   const today = new Date();
  //   const timeDifference =
  //     prediction.ovulation.startDate.getTime() - today.getTime();
  //   const daysLeft = Math.round(timeDifference / (1000 * 60 * 60 * 24));
  //   if (daysLeft == 1) {
  //     return <>{daysLeft} day</>;
  //   }
  //   return <>{daysLeft} days</>;
  // }
  //
  // function nextEvent(prediction: PredictionDTO): string {
  //   if (
  //     prediction.period.startDate.getTime() <
  //     prediction.ovulation.startDate.getTime()
  //   ) {
  //     return {
  //       name: 'Period'
  //     };
  //   }
  //   return 'Ovulation';
  // }
  return Promise.reject(new Error(''));
}
