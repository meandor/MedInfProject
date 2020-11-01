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
