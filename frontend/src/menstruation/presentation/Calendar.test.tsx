import React from 'react';
import { fireEvent, render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Calendar, offsetWithMonday } from './Calendar';
import { utcDate } from '../../core/domain/dateService';

describe('Calendar component', () => {
  it('should render one month for date', () => {
    const currentDate = new Date(2021, 0, 1);

    const { getByText } = render(
      <Calendar
        currentDate={currentDate}
        previousMonths={0}
        upcomingMonths={0}
      />
    );
    const january = getByText(/january/i);

    expect(january).toBeInTheDocument();
  });

  it('should render three months for date', () => {
    const currentDate = new Date(2021, 1, 1);

    const { getByText } = render(
      <Calendar
        currentDate={currentDate}
        previousMonths={1}
        upcomingMonths={1}
        intervalSelectionFn={jest.fn()}
      />
    );
    const january = getByText(/january/i);
    const february = getByText(/february/i);
    const march = getByText(/march/i);

    expect(january).toBeInTheDocument();
    expect(february).toBeInTheDocument();
    expect(march).toBeInTheDocument();
  });

  it('should set start and end date state', async () => {
    const currentDate = new Date(2021, 1, 1);
    const intervalSelectionMock = jest.fn();

    const { getByTestId } = render(
      <Calendar
        currentDate={currentDate}
        previousMonths={1}
        upcomingMonths={1}
        intervalSelectionFn={intervalSelectionMock}
      />
    );
    const firstOfJanuary = getByTestId('2021-1-1');
    const secondOfJanuary = getByTestId('2021-1-2');
    await fireEvent.click(firstOfJanuary);
    await fireEvent.click(secondOfJanuary);

    await expect(intervalSelectionMock).toHaveBeenCalledWith({
      start: new Date(2021, 1, 1),
      end: new Date(2021, 1, 2),
    });
  });

  it('should throw exception if both intervalSelectionFn and onClick are set', () => {
    const actual: any = () =>
      render(
        <Calendar
          currentDate={new Date(2021, 1, 1)}
          previousMonths={1}
          upcomingMonths={1}
          intervalSelectionFn={jest.fn()}
          onClickFn={jest.fn()}
        />
      );

    expect(actual).toThrowError(/not allowed/i);
  });

  it('should execute onClickFn', async () => {
    const onClickMock = jest.fn();

    const { getByTestId } = render(
      <Calendar
        currentDate={new Date(2021, 1, 1)}
        previousMonths={1}
        upcomingMonths={1}
        onClickFn={onClickMock}
      />
    );
    const firstOfJanuary = getByTestId('2021-1-1');
    await fireEvent.click(firstOfJanuary);

    await expect(onClickMock).toHaveBeenCalledWith(new Date(2021, 1, 1));
  });
});

describe('offsetWithMonday', () => {
  it('should do nothing when it already starts with monday', () => {
    const days = [utcDate(2021, 2, 1), utcDate(2021, 2, 2)];

    const actual = offsetWithMonday(days);
    const expected = days;

    expect(actual).toStrictEqual(expected);
  });

  it('should add 4 days from previous month', () => {
    const days = [utcDate(2021, 1, 1), utcDate(2021, 1, 2)];

    const actual = offsetWithMonday(days);
    const expected = [
      utcDate(2021, 1, -3),
      utcDate(2021, 1, -2),
      utcDate(2021, 1, -1),
      utcDate(2021, 1, 0),
      ...days,
    ];

    expect(actual).toStrictEqual(expected);
  });

  it('should add 6 days from previous month when first day is sunday', () => {
    const days = [utcDate(2020, 11, 1)];

    const actual = offsetWithMonday(days);
    const expected = [
      utcDate(2020, 11, -5),
      utcDate(2020, 11, -4),
      utcDate(2020, 11, -3),
      utcDate(2020, 11, -2),
      utcDate(2020, 11, -1),
      utcDate(2020, 11, 0),
      ...days,
    ];

    expect(actual).toStrictEqual(expected);
  });
});
