import React from 'react';
import './registerConfirmation.scss';

export function RegisterConfirmation({
  history,
  location,
}: {
  history: { push: (_: string) => any };
  location: { search: string };
}): JSX.Element {
  const urlParams = new URLSearchParams(location.search);
  const emailFromQuery = urlParams.get('email');
  if (!emailFromQuery || emailFromQuery === '') {
    history.push('/');
  }

  return (
    <section className="register-confirmation">
      <h1>Thank you for registering 🎉</h1>
      <p>
        We will be sending you a confirmation email to{' '}
        <span className="register-confirmation__email" data-testid="email">
          {emailFromQuery}
        </span>{' '}
        so you can confirm your registration.
      </p>
      <p>Please make sure to check your inbox for the confirmation mail.</p>
    </section>
  );
}
