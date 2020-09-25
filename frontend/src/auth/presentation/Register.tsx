import React from 'react';
import { Link } from 'react-router-dom';
import './register.scss';

export function Register(_props: any): JSX.Element {
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
          <form>
            <div className="group">
              <label htmlFor="name">
                Name
                <input type="text" name="text" id="name" data-testid="name" />
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
