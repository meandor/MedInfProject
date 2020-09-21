import React, { ChangeEvent, FormEvent, useState } from 'react';
import { authenticate } from '../domain/loginService';
import './login.scss';

function signIn(
  email: string,
  password: string,
  errorFn: React.Dispatch<React.SetStateAction<string>>,
  successFn: any
) {
  return (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    return authenticate(email, password)
      .then((idToken) => {
        localStorage.setItem('idToken', JSON.stringify(idToken));
        return successFn();
      })
      .catch((error: Error) => {
        errorFn(error.message);
      });
  };
}

function updateState(setStateFn: React.Dispatch<React.SetStateAction<string>>) {
  return (event: ChangeEvent<HTMLInputElement>) => {
    event.preventDefault();
    setStateFn(event.target.value);
  };
}

function ErrorInfo({ errorMessage }: { errorMessage: string }): JSX.Element {
  if (errorMessage !== '') {
    return (
      <section className="error" data-testid="error-info">
        {errorMessage}
      </section>
    );
  }
  return <></>;
}

interface LoginProps {
  history: { push: any };
}

export function Login({ history }: LoginProps): JSX.Element {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const redirectToRoot: () => any = () => history.push('/');

  return (
    <section className="login">
      <section className="login__information">
        <h1>Menstra is the period tracker that respects your privacy.</h1>
        <p>
          We are not sending your data to 3rd party systems in order to analyze
          and track you and follow you around. Menstra was build having your
          data privacy in mind and is completely open source and free.
        </p>
      </section>
      <section className="login__login">
        <section className="login__login__form">
          <h2>Use the period tracker that respects your privacy.</h2>
          <p>Please sign in:</p>
          <ErrorInfo errorMessage={error} />
          <form onSubmit={signIn(email, password, setError, redirectToRoot)}>
            <div className="group">
              <label htmlFor="email">
                E-Mail<sup>*</sup>
                <input
                  type="email"
                  name="email"
                  id="email"
                  data-testid="email"
                  value={email}
                  onChange={updateState(setEmail)}
                  required
                />
              </label>
            </div>
            <div className="group">
              <label htmlFor="password">
                Password<sup>*</sup>
                <input
                  type="password"
                  name="password"
                  id="password"
                  data-testid="password"
                  value={password}
                  onChange={updateState(setPassword)}
                  required
                />
              </label>
            </div>
            <button
              type="submit"
              className="button button-primary"
              data-testid="sign-in"
            >
              Sign in
            </button>
          </form>
        </section>
      </section>
    </section>
  );
}
