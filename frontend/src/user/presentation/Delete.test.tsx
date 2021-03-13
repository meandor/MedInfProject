import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import React from 'react';
import { Delete } from './Delete';
import { unlinkData, deleteData, deleteAccount } from '../domain/userService';

jest.mock('../domain/userService');

const unlinkDataMock = unlinkData as jest.Mock<Promise<void>>;
const deleteDataMock = deleteData as jest.Mock<Promise<void>>;
const deleteAccountMock = deleteAccount as jest.Mock<Promise<void>>;

describe('Delete component', () => {
  it('should display success info', async () => {
    render(<Delete />);

    expect(screen.getByTestId(/unlink-data/i)).toBeInTheDocument();
    expect(screen.getByTestId(/delete-data/i)).toBeInTheDocument();
    expect(screen.getByTestId(/delete-account/i)).toBeInTheDocument();
  });

  describe('unlink data', () => {
    it('should unlink your data when button pressed', async () => {
      unlinkDataMock.mockResolvedValue();
      render(<Delete />);
      const unlinkDataButton = screen.getByTestId(/unlink-data/i);

      fireEvent.click(unlinkDataButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/success/i);
    });

    it('should show error when something went wrong', async () => {
      unlinkDataMock.mockRejectedValue(new Error('foo'));
      render(<Delete />);
      const unlinkDataButton = screen.getByTestId(/unlink-data/i);

      fireEvent.click(unlinkDataButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/error/i);
    });
  });

  describe('delete data', () => {
    it('should delete data when button pressed', async () => {
      deleteDataMock.mockResolvedValue();
      render(<Delete />);
      const deleteDataButton = screen.getByTestId(/delete-data/i);

      fireEvent.click(deleteDataButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/success/i);
    });

    it('should show error when something went wrong', async () => {
      deleteDataMock.mockRejectedValue(new Error('foo'));
      render(<Delete />);
      const deleteDataButton = screen.getByTestId(/delete-data/i);

      fireEvent.click(deleteDataButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/error/i);
    });
  });

  describe('delete account', () => {
    it('should delete account when button pressed', async () => {
      deleteAccountMock.mockResolvedValue();
      render(<Delete />);
      const deleteAccountButton = screen.getByTestId(/delete-account/i);

      fireEvent.click(deleteAccountButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/success/i);
    });

    it('should show error when something went wrong', async () => {
      deleteAccountMock.mockRejectedValue(new Error('foo'));
      render(<Delete />);
      const deleteAccountButton = screen.getByTestId(/delete-account/i);

      fireEvent.click(deleteAccountButton);
      await waitFor(() => screen.getByTestId(/info-box/i));

      expect(screen.getByTestId(/info-box/i)).toHaveTextContent(/error/i);
    });
  });
});
