import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { confirm } from '../domain/registerService';
import { logger } from '../../logger';

function renderFailure(): JSX.Element {
  return (
    <section className="confirmation" data-testid="confirmation">
      <h1>Something went wrong when trying to confirm your account</h1>
      <p>
        Either your account was already confirmed or something else went wrong.
        Please try to <Link to="/login">sign in</Link> to your account.
      </p>
    </section>
  );
}

export function Confirm({
  location,
}: {
  location: { search: string };
}): JSX.Element {
  const urlParams = new URLSearchParams(location.search);
  const idFromQuery = urlParams.get('id');
  const [confirmed, setConfirmed] = useState<null | boolean>(null);

  if (!idFromQuery || idFromQuery === '') {
    logger.error('id missing');
    return renderFailure();
  }

  confirm(idFromQuery)
    .then((_) => setConfirmed(true))
    .catch((error) => {
      logger.error('was not able to confirm user', error);
      setConfirmed(false);
    });

  if (confirmed == null) {
    return (
      <section className="confirmation" data-testid="confirmation">
        <h1>Please wait until we have confirmed your account</h1>
      </section>
    );
  }
  if (confirmed) {
    return (
      <section className="confirmation" data-testid="confirmation">
        <h1>You have successfully confirmed your account</h1>
        <p>
          Please go ahead and <Link to="/login">sign in</Link> to your account.
        </p>
      </section>
    );
  }
  return renderFailure();
}
