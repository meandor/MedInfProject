import React, { FormEvent, useState } from 'react';
import { Calendar, Interval } from './Calendar';
import './create.scss';
import { createPeriod } from '../domain/menstruationService';

export function Create({
  history,
}: {
  history: { push: (_: string) => any };
}): JSX.Element {
  const [interval, setInterval] = useState<undefined | Interval>(undefined);
  const onSubmitHandler: any = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (interval) {
      return createPeriod(interval).then(history.push('/dashboard'));
    }
  };

  return (
    <section className="create">
      <h1 className="create__header">Insert Period</h1>
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
