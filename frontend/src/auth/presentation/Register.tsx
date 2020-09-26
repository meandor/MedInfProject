import React, { FormEvent, useState } from 'react';
import { Link } from 'react-router-dom';
import './register.scss';
import { updateState } from '../../core/presentation/formHelper';
import { logger } from '../../logger';
import { register } from '../domain/registerService';

function createAccount(
  name: string,
  email: string,
  password: string,
  errorFn: React.Dispatch<React.SetStateAction<string>>,
  successFn: any
) {
  return (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    return register(email, password, name)
      .then((user) => {
        successFn();
        return user;
      })
      .catch((error: Error) => {
        logger.error('Was not able to register', error);
        errorFn('E-Mail or password invalid. Please try again.');
      });
  };
}

export function Register(_props: any): JSX.Element {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');

  return (
    <section className="register">
      <section className="register__information">
        <h1>
          Join now and start using Menstra the period tracker that respects your
          privacy
        </h1>
        <p>
          We are not sending your data to 3rd party systems in order to analyze
          and track you and follow you around. Menstra was build having your
          data privacy in mind and is completely open source and free. We not
          only value your privacy but also maintain the concept of{' '}
          <a
            href="https://martinfowler.com/bliki/Datensparsamkeit.html"
            target="_blank"
            rel="noopener noreferrer"
          >
            Datensparsamkeit
          </a>
          .
        </p>
      </section>
      <section className="register__register">
        <section className="register__register__form">
          <h2>Register now to start using Menstra.</h2>
          <p>
            Already have an account? Then sign in <Link to="/login">here</Link>.
          </p>
          <p>Please register:</p>
          <form
            onSubmit={createAccount(
              name,
              email,
              password,
              () => {},
              () => {}
            )}
          >
            <div className="group">
              <label htmlFor="name">
                Name
                <input
                  type="text"
                  name="text"
                  id="name"
                  data-testid="name"
                  value={name}
                  onChange={updateState(setName)}
                />
              </label>
            </div>
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
              data-testid="register"
            >
              Register
            </button>
          </form>
        </section>
      </section>
    </section>
  );
}
