import React from 'react';
import { Menstruation } from '../domain/menstruationService';
import './detail.scss';

export function Detail({
  menstruation,
}: {
  menstruation: Menstruation;
}): JSX.Element {
  return (
    <section className="detail">
      <h3>Period</h3>
      <section className="detail__data">
        <div className="detail__data__label">start:</div>
        <div className="detail__data__value">
          {menstruation.start.toDateString()}
        </div>
        <div className="detail__data__label">end:</div>
        <div className="detail__data__value">
          {menstruation.end.toDateString()}
        </div>
      </section>
    </section>
  );
}
