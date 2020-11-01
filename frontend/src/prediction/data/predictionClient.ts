export interface PredictionDTO {
  ovulation: {
    startDate: Date;
    isActive: boolean;
  };
  period: {
    startDate: Date;
    isActive: boolean;
    duration: number;
  };
}

export function getPrediction(_email: string): Promise<PredictionDTO> {
  return Promise.reject(new Error());
}
