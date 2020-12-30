import React from 'react';
import './create.scss';

export function Create({
  history,
}: {
  history: { push: (_: string) => any };
}): JSX.Element {
  return (
    <section className="create">
      <h1 className="create__header">Insert Period</h1>
      <form>
        <section className="create__calendar" data-testid="calendar" />
        <section className="create__actions">
          <button
            type="submit"
            className="button button-secondary"
            onClick={history.push('/dashboard')}
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
