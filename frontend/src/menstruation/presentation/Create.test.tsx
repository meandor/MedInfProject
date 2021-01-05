import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render } from '@testing-library/react';
import { Create } from './Create';
import { Calendar, Interval } from './Calendar';
import { createPeriod, Period } from '../domain/menstruationService';

jest.mock('./Calendar');
jest.mock('../domain/menstruationService');

describe('Create component', () => {
  let historyMock: any;
  let pushMock: jest.Mock;
  let title: HTMLElement;
  let calendar: HTMLElement;
  let saveButton: HTMLElement;
  let cancelButton: HTMLElement;
  const calendarMock = Calendar as jest.Mock<JSX.Element>;
  const createPeriodMock = createPeriod as jest.Mock<Promise<Period>>;
  const periodInterval: Interval = {
    start: new Date(2021, 1, 1),
    end: new Date(2021, 1, 5),
  };

  beforeEach(() => {
    pushMock = jest.fn();
    historyMock = {
      push: pushMock,
    };
    calendarMock.mockImplementation(({ intervalSelectionFn }) => {
      const update: () => JSX.Element = () =>
        intervalSelectionFn(periodInterval);
      return (
        <section data-testid="calendar" onClick={update} aria-hidden="true" />
      );
    });
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

  it('should save given interval', async () => {
    createPeriodMock.mockResolvedValue({
      start: new Date(),
      end: new Date(),
    });

    await fireEvent.click(calendar);
    await fireEvent.click(saveButton);

    await expect(createPeriodMock).toBeCalledWith(periodInterval);
    await expect(pushMock).toBeCalledWith('/dashboard');
  });
});
