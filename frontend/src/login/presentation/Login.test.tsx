import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';
import { Login } from './Login';

describe('render component', () => {
  let signInButton: HTMLElement;
  let emailField: HTMLElement;
  let passwordField: HTMLElement;

  beforeEach(() => {
    const { getByTestId } = render(<Login />);
    signInButton = getByTestId(/sign-in/i);
    emailField = getByTestId(/email/i);
    passwordField = getByTestId(/password/i);
  });

  it('should render component in empty state', () => {
    expect(signInButton).toBeInTheDocument();
    expect(emailField).toBeInTheDocument();
    expect(passwordField).toBeInTheDocument();
  });
});
