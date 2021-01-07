import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import React from 'react';
import { RegisterConfirmation } from './RegisterConfirmation';

describe('RegisterConfirmation', () => {
  it('should render with the given email', () => {
    const email = 'foo@bar.com';
    const location = {
      search: `?email=${email}`,
    };

    const { getByTestId } = render(
      <MemoryRouter>
        <RegisterConfirmation
          location={location}
          history={{ push: jest.fn() }}
        />
      </MemoryRouter>
    );
    const emailField = getByTestId(/email/i);

    expect(emailField.textContent).toBe(email);
  });

  it('should redirect to root when no email given', () => {
    const location = {
      search: '?email=',
    };
    const history = {
      push: jest.fn(),
    };

    render(
      <MemoryRouter>
        <RegisterConfirmation location={location} history={history} />
      </MemoryRouter>
    );

    expect(history.push).toHaveBeenCalledWith('/');
  });
});
