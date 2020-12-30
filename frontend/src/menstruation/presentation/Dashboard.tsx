import React, { useEffect, useState } from 'react';
import { Event, predict, Prediction } from '../domain/predictionService';
import './dashboard.scss';
import { logger } from '../../logger';

function withDaysUnit(days: number): JSX.Element {
  if (days === 1) {
    return (
      <>
        {days}
        <span className="dashboard__prediction__gauge__meter__unit"> day</span>
      </>
    );
  }
  return (
    <>
      {days}
      <span className="dashboard__prediction__gauge__meter__unit"> days</span>
    </>
  );
}

function NoData(_props: any): JSX.Element {
  return (
    <>
      <h3>No data available.</h3>
      You could start by adding your last period.
      <p>
        <button type="button" className="button button-primary">
          Insert period
        </button>
      </p>
    </>
  );
}

function gaugeSupportClass(prediction: Prediction): string {
  if (prediction.event === Event.PERIOD && prediction.isUpcoming) {
    return 'upcoming-period';
  }

  if (prediction.event === Event.PERIOD && !prediction.isUpcoming) {
    return 'period';
  }

  if (prediction.event === Event.OVULATION && prediction.isUpcoming) {
    return 'upcoming-ovulation';
  }

  return 'ovulation';
}

function PredictionInfo({
  prediction,
}: {
  prediction: Prediction;
}): JSX.Element {
  if (prediction.isUpcoming) {
    return (
      <div
        className={`dashboard__prediction__gauge ${gaugeSupportClass(
          prediction
        )}`}
      >
        <p className="dashboard__prediction__gauge__info">
          Your {prediction.event}
          <br />
          <span>will be in</span>
        </p>
        <p className="dashboard__prediction__gauge__meter-bottom">
          {withDaysUnit(prediction.days)}
        </p>
      </div>
    );
  }
  if (prediction.event === Event.PERIOD) {
    return (
      <div
        className={`dashboard__prediction__gauge ${gaugeSupportClass(
          prediction
        )}`}
      >
        <p className="dashboard__prediction__gauge__meter-top">
          {withDaysUnit(prediction.days)}
        </p>
        <p className="dashboard__prediction__gauge__info">
          left in your
          <br />
          {prediction.event}
        </p>
      </div>
    );
  }

  return (
    <div
      className={`dashboard__prediction__gauge ${gaugeSupportClass(
        prediction
      )}`}
    >
      <p className="dashboard__prediction__gauge__info">
        Today is your <br />
        <span className="big">Ovulation</span>
      </p>
    </div>
  );
}

export function Dashboard({
  history,
}: {
  history: { push: (_: string) => any };
}): JSX.Element {
  const [prediction, setPrediction] = useState<Prediction | undefined>(
    undefined
  );

  useEffect(() => {
    predict()
      .then(setPrediction)
      .catch((error) => {
        logger.error('Was not able to get prediction', error);
        setPrediction(undefined);
      });
  }, []);

  const goToInsert: any = () => history.push('create');

  if (prediction) {
    return (
      <section data-testid="prediction-field" className="dashboard">
        <section className="dashboard__prediction">
          <PredictionInfo prediction={prediction} />
          <p>
            <button
              type="button"
              className="button button-primary"
              onClick={goToInsert}
            >
              Insert period
            </button>
          </p>
        </section>
      </section>
    );
  }

  return (
    <section data-testid="prediction-field" className="dashboard">
      <section className="dashboard__prediction">
        <NoData />
      </section>
    </section>
  );
}
