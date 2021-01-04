import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render } from '@testing-library/react';
import { Create } from './Create';
import { Calendar } from './Calendar';
// import { createPeriod } from '../domain/menstruationService';

jest.mock('./Calendar');

describe('Create component', () => {
  let historyMock: any;
  let title: HTMLElement;
  let calendar: HTMLElement;
  let saveButton: HTMLElement;
  let cancelButton: HTMLElement;
  const calendarMock = Calendar as jest.Mock<JSX.Element>;

  beforeEach(() => {
    historyMock = {
      push: jest.fn(),
    };
    calendarMock.mockReturnValue(<section data-testid="calendar" />);
    const { getByText, getByTestId } = render(<Create history={historyMock} />);
    title = getByText(/insert/i);
    calendar = getByTestId(/calendar/i);
    saveButton = getByText(/save/i);
    cancelButton = getByText(/cancel/i);
  });

  it('should render create form', () => {
    expect(title).toBeInTheDocument();
    expect(calendar).toBeInTheDocument();
    expect(saveButton).toBeInTheDocument();
    expect(cancelButton).toBeInTheDocument();
  });

  it('should go back when cancel is clicked', async () => {
    await fireEvent.click(cancelButton);

    await expect(historyMock.push).toBeCalledWith('/dashboard');
  });
});
