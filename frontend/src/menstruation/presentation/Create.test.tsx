import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { fireEvent, render } from '@testing-library/react';
import { Create } from './Create';
// import { createPeriod } from '../domain/menstruationService';

// jest.mock('../domain/menstruationService');

describe('Create component', () => {
  let historyMock: any;
  let title: HTMLElement;
  let calendar: HTMLElement;
  let saveButton: HTMLElement;
  let cancelButton: HTMLElement;

  beforeEach(() => {
    historyMock = {
      push: jest.fn(),
    };
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
