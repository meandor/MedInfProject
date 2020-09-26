import React from 'react';

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
      Thank your for registering. We will be sending you a confirmation email to{' '}
      <span className="email" data-testid="email">
        {emailFromQuery}
      </span>{' '}
      so you can confirm your registration.
    </section>
  );
}
