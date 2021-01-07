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

function isActive(
  day: Date,
  setStateFn: ((date: Date) => any) | undefined,
  startDate: Date | null,
  endDate: Date | null
): boolean {
  if (setStateFn === undefined) {
    return true;
  }
  return (
    (startDate && startDate.getTime() === day.getTime()) ||
    (endDate && endDate.getTime() === day.getTime()) ||
    ((startDate &&
      endDate &&
      startDate.getTime() < day.getTime() &&
      endDate.getTime() > day.getTime()) as boolean)
  );
}

function renderDayElement(
  day: Date,
  setStateFn: ((date: Date) => any) | undefined,
  startDate: Date | null,
  endDate: Date | null
): JSX.Element {
  if (isActive(day, setStateFn, startDate, endDate)) {
    return <div className="active">{day.getDate()}</div>;
  }
  return <>{day.getDate()}</>;
}

function dayStateClass(day: Date): string {
  let state = '';
  if (Math.floor((day.getDate() - 1) / 7) % 2 === 0) {
    state += 'gray';
  }
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  if (today.getTime() === day.getTime()) {
    state += ' today';
  }
  return state;
}

function renderDay(
  setStateFn: ((date: Date) => any) | undefined,
  currentStartDate: Date | null,
  currentEndDate: Date | null
): (day: Date) => JSX.Element {
  return (day: Date) => {
    if (setStateFn === undefined) {
      return (
        <section
          key={`${day.getFullYear()}-${day.getMonth()}-${day.getDate()}`}
          data-testid={`${day.getFullYear()}-${day.getMonth()}-${day.getDate()}`}
          className={`calendar__month__day ${dayStateClass(day)}`}
        >
          {renderDayElement(day, setStateFn, currentStartDate, currentEndDate)}
        </section>
      );
    }
    return (
      <section
        key={`${day.getFullYear()}-${day.getMonth()}-${day.getDate()}`}
        data-testid={`${day.getFullYear()}-${day.getMonth()}-${day.getDate()}`}
        className={`calendar__month__day ${dayStateClass(day)}`}
        onClick={() => setStateFn(day)}
        aria-hidden="true"
      >
        {renderDayElement(day, setStateFn, currentStartDate, currentEndDate)}
      </section>
    );
  };
}

function renderMonth(
  setStateFn: ((date: Date) => any) | undefined,
  currentStartDate: Date | null,
  currentEndDate: Date | null
): (monthDate: Date) => JSX.Element {
  return (monthDate) => {
    const month = monthDate.toLocaleString('en-us', { month: 'long' });
    const lastDay = new Date(
      monthDate.getFullYear(),
      monthDate.getMonth() + 1,
      0
    );
    const days = rest(range(lastDay.getDate())).map(
      (day) => new Date(monthDate.getFullYear(), monthDate.getMonth(), day)
    );
    return (
      <section className="calendar__month" key={monthDate.getMonth()}>
        <section className="calendar__month__title">
          {`${month} ${monthDate.getFullYear()}`}
        </section>
        {days.map(renderDay(setStateFn, currentStartDate, currentEndDate))}
      </section>
    );
  };
}

function toggleState(
  startDate: Date | null,
  setStartDateFn: any,
  endDate: Date | null,
  setEndDateFn: any,
  intervalSelectionFn: ((interval: Interval) => any) | undefined
): ((date: Date) => any) | undefined {
  if (intervalSelectionFn === undefined) {
    return undefined;
  }
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
  intervalSelectionFn?: ((interval: Interval) => any);
}): JSX.Element {
  const [startDate, setStartDate] = useState<null | Date>(null);
  const [endDate, setEndDate] = useState<null | Date>(null);

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
