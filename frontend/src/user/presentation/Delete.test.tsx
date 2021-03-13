import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import React from 'react';
import { Delete } from './Delete';
import { unlinkData } from '../domain/userService';

jest.mock('../domain/userService');

const unlinkDataMock = unlinkData as jest.Mock<Promise<void>>;

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
});
