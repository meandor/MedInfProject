import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';
import { Register } from './Register';
import {MemoryRouter} from "react-router";

describe('Register component', () => {
  let registerButton: HTMLElement;
  let emailField: HTMLElement;
  let passwordField: HTMLElement;
  let nameField: HTMLElement;

  beforeEach(() => {
    const { getByTestId } = render(<MemoryRouter><Register /></MemoryRouter>);
    registerButton = getByTestId(/register/i);
    emailField = getByTestId(/email/i);
    passwordField = getByTestId(/password/i);
    nameField = getByTestId(/name/i);
  });

  it('should render component in empty state', () => {
    expect(registerButton).toBeInTheDocument();
    expect(emailField).toBeInTheDocument();
    expect(passwordField).toBeInTheDocument();
    expect(nameField).toBeInTheDocument();
  });
});
