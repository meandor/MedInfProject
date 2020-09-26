import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { Register } from './Register';
import { User, register } from '../domain/registerService';

jest.mock('../domain/registerService');

const registerMock = register as jest.Mock<Promise<User>>;

describe('Register component', () => {
  let registerButton: HTMLElement;
  let emailField: HTMLElement;
  let passwordField: HTMLElement;
  let nameField: HTMLElement;
  let pushMock: jest.Mock;
  const emailValue = 'foo@bar.com';

  beforeEach(() => {
    pushMock = jest.fn();
    const { getByTestId } = render(
      <MemoryRouter>
        <Register history={{ push: pushMock }} />
      </MemoryRouter>
    );
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

  it('should set state accordingly', () => {
    fireEvent.change(emailField, { target: { value: emailValue } });
    fireEvent.change(passwordField, { target: { value: 'password' } });
    fireEvent.change(nameField, { target: { value: 'foo bar' } });

    expect(emailField).toHaveValue(emailValue);
    expect(passwordField).toHaveValue('password');
    expect(nameField).toHaveValue('foo bar');
  });

  it('should send state to service', async () => {
    registerMock.mockResolvedValue({
      email: emailValue,
      password: '',
      name: 'foo bar',
      emailIsVerified: false,
    });
    fireEvent.change(emailField, { target: { value: emailValue } });
    fireEvent.change(passwordField, { target: { value: 'password' } });
    fireEvent.change(nameField, { target: { value: 'foo bar' } });

    await fireEvent.click(registerButton);

    expect(registerMock).toHaveBeenCalledWith(
      emailValue,
      'password',
      'foo bar'
    );
    expect(pushMock).toHaveBeenCalledWith(
      `/register/confirmation?email=${emailValue}`
    );
  });
});
