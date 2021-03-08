import React from 'react';
import { render } from '@testing-library/react';
import { createMemoryHistory } from 'history';
import { Router } from 'react-router';
import '@testing-library/jest-dom/extend-expect';
import { authenticatedUser, IDToken } from '../domain/loginService';
import { ProfileMenu } from './ProfileMenu';

jest.mock('../domain/loginService');

const authenticatedUserMock = authenticatedUser as jest.Mock<
  IDToken | undefined
>;

describe('ProfileMenu', () => {
  it('should render nothing when not logged in', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    const { queryByText } = render(<ProfileMenu />);
    const userName = queryByText(/foo bar/i);

    expect(userName).not.toBeInTheDocument();
  });

  it('should render name when available', () => {
    authenticatedUserMock.mockReturnValue({
      email: 'foo@bar.com',
      email_verified: true,
      name: 'foo bar',
      sub: '1337',
    });
    const { getByText } = render(
      <Router history={createMemoryHistory()}>
        <ProfileMenu />
      </Router>
    );
    const userName = getByText(/foo bar/i);

    expect(userName).toBeInTheDocument();
  });

  it('should render email when name not available', () => {
    authenticatedUserMock.mockReturnValue({
      email: 'foo@bar.com',
      email_verified: true,
      name: '',
      sub: '1337',
    });
    const { queryByText, getAllByText } = render(
      <Router history={createMemoryHistory()}>
        <ProfileMenu />
      </Router>
    );
    const userName = queryByText(/foo bar/i);
    const email = getAllByText(/foo@bar.com/i);

    expect(userName).not.toBeInTheDocument();
    expect(email.length).toBe(2);
  });
});
