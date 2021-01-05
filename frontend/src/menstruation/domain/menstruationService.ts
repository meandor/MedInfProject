export interface Period {
  start: Date;
  end: Date;
}

export function createPeriod(_period: Period): Promise<Period> {
  return Promise.reject(new Error('foo'));
}
