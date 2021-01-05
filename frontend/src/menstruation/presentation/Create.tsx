import React, { FormEvent, useState } from 'react';
import { Calendar, Interval } from './Calendar';
import './create.scss';
import { createPeriod } from '../domain/menstruationService';
import { logger } from '../../logger';
import { ErrorInfo } from '../../core/presentation/ErrorInfo';

export function Create({
  history,
}: {
  history: { push: (_: string) => any };
}): JSX.Element {
  const [interval, setInterval] = useState<undefined | Interval>(undefined);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const onSubmitHandler: any = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrorMessage('');
    if (interval) {
      return createPeriod(interval)
        .then(history.push('/dashboard'))
        .catch((error) => {
          logger.error('Was not able to create period', error);
          setErrorMessage(
            'There was an error creating that period. Please try again or check back later.'
          );
        });
    }
  };

  return (
    <section className="create">
      <h1 className="create__header">Insert Period</h1>
      <ErrorInfo errorMessage={errorMessage} data-testid="error" />
      <form onSubmit={onSubmitHandler}>
        <Calendar
          data-testid="calendar"
          upcomingMonths={1}
          previousMonths={1}
          currentDate={new Date()}
          intervalSelectionFn={setInterval}
        />
        <section className="create__actions">
          <button
            type="submit"
            className="button button-secondary"
            onClick={() => history.push('/dashboard')}
          >
            Cancel
          </button>
          <button type="submit" className="button button-primary">
            Save
          </button>
        </section>
      </form>
    </section>
  );
}
