import React from 'react';
import { render } from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';
import { Menstruation } from '../domain/menstruationService';
import { Detail } from './Detail';

describe('render detail component', () => {
  it('should render given day', () => {
    const start = new Date(2021, 1, 1);
    const end = new Date(2021, 1, 5);
    const menstruation: Menstruation = { start, end };

    const { getByText } = render(<Detail menstruation={menstruation} />);

    expect(getByText(/period/i)).toBeInTheDocument();
    expect(getByText(/start/i)).toBeInTheDocument();
    expect(getByText(/end/i)).toBeInTheDocument();
  });
});
