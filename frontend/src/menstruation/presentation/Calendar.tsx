import React from 'react';
import './calendar.scss';

function range(endInclusive: number): Array<number> {
  return Array.from(Array(endInclusive + 1).keys());
}

function rest(array: Array<number>): Array<number> {
  const [, ...tail] = array;
  return tail;
}

function renderDay(month: number) {
  return (day: number) => {
    if (Math.floor((day - 1) / 7) % 2 === 0) {
      return (
        <section key={`${month}-${day}`} className="calendar__month__day">
          {day}
        </section>
      );
    }
    return (
      <section key={`${month}-${day}`} className="calendar__month__day gray">
        {day}
      </section>
    );
  };
}

function renderMonth(monthDate: Date): JSX.Element {
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
      {days.map(renderDay(monthDate.getMonth()))}
    </section>
  );
}

export function Calendar({
  currentDate,
  previousMonths,
  upcomingMonths,
}: {
  currentDate: Date;
  previousMonths: number;
  upcomingMonths: number;
}): JSX.Element {
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
  return <section className="calendar">{monthRange.map(renderMonth)}</section>;
}
