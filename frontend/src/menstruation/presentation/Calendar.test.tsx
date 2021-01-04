import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Calendar } from './Calendar';

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
      />
    );
    const january = getByText(/january/i);
    const february = getByText(/february/i);
    const march = getByText(/march/i);

    expect(january).toBeInTheDocument();
    expect(february).toBeInTheDocument();
    expect(march).toBeInTheDocument();
  });
});
