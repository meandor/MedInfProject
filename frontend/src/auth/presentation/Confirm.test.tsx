import { render, act } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { MemoryRouter } from 'react-router';
import React from 'react';
import { confirm, User } from '../domain/registerService';
import { Confirm } from './Confirm';

jest.mock('../domain/registerService');

const confirmMock = confirm as jest.Mock<Promise<User>>;

describe('Confirm', () => {
  it('should display success info', async () => {
    confirmMock.mockResolvedValue({
      email: 'foo@bar.com',
      password: 'password',
      name: 'foo bar',
      isVerified: true,
    });
    const id = '42';
    const location = {
      search: `?id=${id}`,
    };
    let confirmationField: HTMLElement | null = null;

    await act(async () => {
      const { getByTestId } = render(
        <MemoryRouter>
          <Confirm location={location} />
        </MemoryRouter>
      );
      confirmationField = getByTestId(/confirmation/i);
    });

    await expect(confirmationField).toHaveTextContent(
      /successfully confirmed/i
    );
    await expect(confirmMock).toHaveBeenCalledWith(id);
  });

  it('should display failing info', async () => {
    confirmMock.mockRejectedValue(new Error('expected error'));
    const id = '42';
    const location = {
      search: `?id=${id}`,
    };
    let confirmationField: HTMLElement | null = null;

    await act(async () => {
      const { getByTestId } = render(
        <MemoryRouter>
          <Confirm location={location} />
        </MemoryRouter>
      );
      confirmationField = getByTestId(/confirmation/i);
    });

    await expect(confirmationField).toHaveTextContent(/went wrong/i);
    await expect(confirmMock).toHaveBeenCalledWith(id);
  });
});
