import React, { useEffect, useState } from 'react';
import { Event, predict, Prediction } from '../domain/predictionService';
import './dashboard.scss';
import { logger } from '../../logger';
import { Calendar } from './Calendar';
import { find, Menstruation } from '../domain/menstruationService';
import { Detail } from './Detail';

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

function NoData({ insertFn }: { insertFn: any }): JSX.Element {
  return (
    <>
      <h3>No data available.</h3>
      You could start by adding your last period.
      <p>
        <button
          type="button"
          className="button button-primary"
          onClick={insertFn}
        >
          Insert period
        </button>
      </p>
    </>
  );
}

function gaugeSupportClass(prediction: Prediction): string {
  if (prediction.event === Event.MENSTRUATION && prediction.isUpcoming) {
    return 'upcoming-period';
  }

  if (prediction.event === Event.MENSTRUATION && !prediction.isUpcoming) {
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
  if (prediction.event === Event.MENSTRUATION) {
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

function PredictionComponent({
  prediction,
  insertFn,
}: {
  prediction: Prediction | null;
  insertFn: any;
}): JSX.Element {
  if (prediction) {
    return (
      <section data-testid="prediction-field" className="dashboard__prediction">
        <PredictionInfo prediction={prediction} />
        <p>
          <button
            type="button"
            className="button button-primary"
            onClick={insertFn}
          >
            Insert period
          </button>
        </p>
      </section>
    );
  }

  return (
    <section className="dashboard__prediction" data-testid="prediction-field">
      <NoData insertFn={insertFn} />
    </section>
  );
}

function renderDetailCollapsable(
  activeMenstruation: Menstruation | null
): JSX.Element {
  if (activeMenstruation) {
    return (
      <div className="dashboard__calendar__detail">
        <Detail menstruation={activeMenstruation} />
      </div>
    );
  }
  return <div className="dashboard__calendar__detail-hidden" />;
}

function showDetail(selectMenstruationFn: any, menstruation: Menstruation[]) {
  return (day: Date) => {
    const selectedTime = day.getTime();
    const selectedMenstruation = menstruation.find(
      (m) =>
        m.start.getTime() <= selectedTime && selectedTime <= m.end.getTime()
    );
    if (selectedMenstruation) {
      selectMenstruationFn(selectedMenstruation);
    } else {
      selectMenstruationFn(null);
    }
  };
}

export function Dashboard({
  history,
}: {
  history: { push: (_: string) => any };
}): JSX.Element {
  const [prediction, setPrediction] = useState<Prediction | null>(null);
  const [menstruation, setMenstruation] = useState<Menstruation[]>([]);
  const [
    activeMenstruation,
    selectMenstruation,
  ] = useState<Menstruation | null>(null);

  useEffect(() => {
    predict()
      .then(setPrediction)
      .catch((error) => {
        logger.error('Was not able to get prediction', error);
        setPrediction(null);
      });
    find()
      .then(setMenstruation)
      .catch((error) => {
        logger.error('Was not able to get menstruation', error);
        setMenstruation([{ start: new Date(), end: new Date(2021, 2, 3) }]);
      });
    setMenstruation([{ start: new Date(), end: new Date(2021, 2, 3) }]);
  }, []);

  const goToInsert: any = () => history.push('/create');
  return (
    <section className="dashboard">
      <PredictionComponent prediction={prediction} insertFn={goToInsert} />
      <section className="dashboard__calendar">
        {renderDetailCollapsable(activeMenstruation)}
        <div className="dashboard__calendar__data">
          <Calendar
            currentDate={new Date()}
            previousMonths={2}
            upcomingMonths={3}
            activeIntervals={menstruation}
            onClickFn={showDetail(selectMenstruation, menstruation)}
            data-testid="calendar"
          />
        </div>
        <div className="dashboard__calendar__actions">
          <button
            type="button"
            onClick={goToInsert}
            className="button button-secondary"
          >
            Insert period
          </button>
        </div>
      </section>
    </section>
  );
}
