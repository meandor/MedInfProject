import React, { useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import {
  deleteMenstruation,
  Menstruation,
} from '../domain/menstruationService';
import './detail.scss';
import { logger } from '../../logger';

function deleteWithErrorHandler(
  setErrorFn: (errorState: boolean) => void,
  menstruation: Menstruation
) {
  return () => {
    setErrorFn(false);
    deleteMenstruation(menstruation).catch((error) => {
      logger.error('Was not able to delete Period', error);
      setErrorFn(true);
    });
  };
}

function renderError(error: boolean): JSX.Element {
  if (error) {
    return (
      <p className="detail__error">
        There was an error while deleting. Please try again or wait a bit before
        doing so.
      </p>
    );
  }
  return <></>;
}

export function Detail({
  menstruation,
}: {
  menstruation: Menstruation;
}): JSX.Element {
  const [error, setError] = useState<boolean>(false);

  return (
    <section className="detail">
      <h3>Period</h3>
      {renderError(error)}
      <section className="detail__data">
        <div className="detail__data__label">start:</div>
        <div className="detail__data__value">
          {menstruation.start.toDateString()}
        </div>
        <div className="detail__data__label">end:</div>
        <div className="detail__data__value">
          {menstruation.end.toDateString()}
        </div>
      </section>
      <section className="detail__actions">
        <button
          type="submit"
          className="button button-secondary"
          onClick={deleteWithErrorHandler(setError, menstruation)}
        >
          <FontAwesomeIcon icon={faTrash} /> Delete
        </button>
      </section>
    </section>
  );
}
