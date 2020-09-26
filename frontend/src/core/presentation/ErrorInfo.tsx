import React from 'react';

export function ErrorInfo({
  errorMessage,
}: {
  errorMessage: string;
}): JSX.Element {
  if (errorMessage !== '') {
    return (
      <section className="error" data-testid="error-info">
        {errorMessage}
      </section>
    );
  }
  return <></>;
}
