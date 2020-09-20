import React from 'react';
import './login.scss';

export function Login(_props: any): JSX.Element {
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
          <form>
            <div className="group">
              <label htmlFor="email">
                E-Mail<sup>*</sup>
                <input
                  type="email"
                  name="email"
                  id="email"
                  data-testid="email"
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
