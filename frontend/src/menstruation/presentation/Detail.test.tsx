import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import {
  Menstruation,
  deleteMenstruation,
} from '../domain/menstruationService';
import { Detail } from './Detail';

jest.mock('../domain/menstruationService');

describe('Detail component', () => {
  const deleteMenstruationMock = deleteMenstruation as jest.Mock<Promise<void>>;

  it('should render given day', () => {
    const start = new Date(2021, 1, 1);
    const end = new Date(2021, 1, 5);
    const menstruation: Menstruation = { start, end };

    const { getByText } = render(<Detail menstruation={menstruation} />);

    expect(getByText(/period/i)).toBeInTheDocument();
    expect(getByText(/start/i)).toBeInTheDocument();
    expect(getByText(/end/i)).toBeInTheDocument();
  });

  it('should delete when clicked', async () => {
    const start = new Date(2021, 1, 1);
    const end = new Date(2021, 1, 5);
    const menstruation: Menstruation = { start, end };
    deleteMenstruationMock.mockResolvedValue();
    const { getByText } = render(<Detail menstruation={menstruation} />);

    const deleteButton = getByText(/delete/i);

    await fireEvent.click(deleteButton);

    await expect(deleteButton).toBeInTheDocument();
    await expect(deleteMenstruationMock).toHaveBeenCalledWith(menstruation);
  });

  it('should display error when delete failed', async () => {
    const start = new Date(2021, 1, 1);
    const end = new Date(2021, 1, 5);
    const menstruation: Menstruation = { start, end };
    deleteMenstruationMock.mockRejectedValue(new Error('foo'));
    const { getByText } = render(<Detail menstruation={menstruation} />);

    const deleteButton = getByText(/delete/i);

    await fireEvent.click(deleteButton);

    await expect(deleteButton).toBeInTheDocument();
    await expect(deleteMenstruationMock).toHaveBeenCalledWith(menstruation);
    await expect(getByText(/error/i)).toBeInTheDocument();
  });
});
