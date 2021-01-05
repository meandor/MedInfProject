import React, { useState } from 'react';
import './calendar.scss';

export interface Interval {
  start: Date;
  end: Date;
}

function range(endInclusive: number): Array<number> {
  return Array.from(Array(endInclusive + 1).keys());
}

function rest(array: Array<number>): Array<number> {
  const [, ...tail] = array;
  return tail;
}

function renderDay(
  setStateFn: (date: Date) => any,
  currentStartDate: undefined | Date,
  currentEndDate: undefined | Date,
  month: number,
  year: number
): (day: number) => JSX.Element {
  return (day: number) => {
    let state = '';
    if (Math.floor((day - 1) / 7) % 2 === 0) {
      state += 'gray';
    }
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const currentDay = new Date(year, month, day);
    if (today.getTime() === currentDay.getTime()) {
      state += ' today';
    }

    let dayElement: JSX.Element = <>{day}</>;
    if (
      (currentStartDate &&
        currentStartDate.getTime() === currentDay.getTime()) ||
      (currentEndDate && currentEndDate.getTime() === currentDay.getTime()) ||
      (currentStartDate &&
        currentEndDate &&
        currentStartDate.getTime() < currentDay.getTime() &&
        currentEndDate.getTime() > currentDay.getTime())
    ) {
      dayElement = <div className="active">{day}</div>;
    }

    return (
      <section
        key={`${year}-${month}-${day}`}
        data-testid={`${year}-${month}-${day}`}
        className={`calendar__month__day ${state}`}
        onClick={() => setStateFn(currentDay)}
        aria-hidden="true"
      >
        {dayElement}
      </section>
    );
  };
}

function renderMonth(
  setStateFn: (date: Date) => any,
  currentStartDate: undefined | Date,
  currentEndDate: undefined | Date
): (monthDate: Date) => JSX.Element {
  return (monthDate) => {
    const month = monthDate.toLocaleString('en-us', { month: 'long' });
    const lastDay = new Date(
      monthDate.getFullYear(),
      monthDate.getMonth() + 1,
      0
    );
    const days = rest(range(lastDay.getDate()));
    return (
      <section className="calendar__month" key={monthDate.getMonth()}>
        <section className="calendar__month__title">
          {`${month} ${monthDate.getFullYear()}`}
        </section>
        {days.map(
          renderDay(
            setStateFn,
            currentStartDate,
            currentEndDate,
            monthDate.getMonth(),
            monthDate.getFullYear()
          )
        )}
      </section>
    );
  };
}

function toggleState(
  startDate: undefined | Date,
  setStartDateFn: any,
  endDate: undefined | Date,
  setEndDateFn: any,
  intervalSelectionFn: (interval: Interval) => any
): (date: Date) => any {
  return (date: Date) => {
    if (!startDate && !endDate) {
      setStartDateFn(date);
      intervalSelectionFn({ start: date, end: date });
    }
    if (startDate && !endDate && startDate.getTime() < date.getTime()) {
      setEndDateFn(date);
      intervalSelectionFn({ start: startDate, end: date });
    } else {
      setStartDateFn(date);
      setEndDateFn(undefined);
      intervalSelectionFn({ start: date, end: date });
    }
  };
}

export function Calendar({
  currentDate,
  previousMonths,
  upcomingMonths,
  intervalSelectionFn,
}: {
  currentDate: Date;
  previousMonths: number;
  upcomingMonths: number;
  intervalSelectionFn: (interval: Interval) => any;
}): JSX.Element {
  const [startDate, setStartDate] = useState<undefined | Date>(undefined);
  const [endDate, setEndDate] = useState<undefined | Date>(undefined);

  const upcomingRange = rest(range(upcomingMonths));
  const upcomingMonthRange = upcomingRange.map(
    (monthIncrease) =>
      new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + monthIncrease,
        1
      )
  );

  const previousRange = rest(range(previousMonths));
  const previousMonthRange = previousRange
    .reverse()
    .map(
      (monthIncrease) =>
        new Date(
          currentDate.getFullYear(),
          currentDate.getMonth() - monthIncrease,
          1
        )
    );

  const currentMonth = [
    new Date(currentDate.getFullYear(), currentDate.getMonth(), 1),
  ];
  const monthRange = previousMonthRange
    .concat(currentMonth)
    .concat(upcomingMonthRange);
  const stateFn = toggleState(
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    intervalSelectionFn
  );
  return (
    <section className="calendar">
      {monthRange.map(renderMonth(stateFn, startDate, endDate))}
    </section>
  );
}
