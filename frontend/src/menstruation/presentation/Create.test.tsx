import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { act, fireEvent, render } from '@testing-library/react';
import { Create } from './Create';
import { Calendar, Interval } from './Calendar';
import {
  createMenstruation,
  Menstruation,
} from '../domain/menstruationService';

jest.mock('./Calendar');
jest.mock('../domain/menstruationService');

describe('Create component', () => {
  const createMenstruationMock = createMenstruation as jest.Mock<
    Promise<Menstruation>
  >;
  const interval: Interval = {
    start: new Date(2021, 1, 1),
    end: new Date(2021, 1, 5),
  };
  const calendarMock = (Calendar as unknown) as jest.Mock;
  let historyMock: { push: jest.Mock };
  let pushMock: jest.Mock;

  beforeEach(() => {
    pushMock = jest.fn();
    historyMock = {
      push: pushMock,
    };
    calendarMock.mockImplementation(({ intervalSelectionFn }) => {
      const update: () => JSX.Element = () => intervalSelectionFn(interval);
      return (
        <section data-testid="calendar" onClick={update} aria-hidden="true" />
      );
    });
  });

  describe('valid behaviour', () => {
    let title: HTMLElement;
    let calendar: HTMLElement;
    let saveButton: HTMLElement;
    let cancelButton: HTMLElement;

    beforeEach(() => {
      pushMock = jest.fn();
      historyMock = {
        push: pushMock,
      };
      calendarMock.mockImplementation(({ intervalSelectionFn }) => {
        const update: () => JSX.Element = () => intervalSelectionFn(interval);
        return (
          <section data-testid="calendar" onClick={update} aria-hidden="true" />
        );
      });
      const { getByText, getByTestId } = render(
        <Create history={historyMock} />
      );
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
      createMenstruationMock.mockResolvedValue({
        start: new Date(),
        end: new Date(),
      });

      await fireEvent.click(calendar);
      await fireEvent.click(saveButton);

      await expect(createMenstruationMock).toBeCalledWith(interval);
      await expect(pushMock).toBeCalledWith('/dashboard');
    });
  });

  describe('error handling', () => {
    it('should show creation error', () =>
      act(async () => {
        createMenstruationMock.mockRejectedValue(new Error('foo'));
        const { getByText, getByTestId } = render(
          <Create history={historyMock} />
        );
        const calendar = getByTestId(/calendar/i);
        const saveButton = getByText(/save/i);

        await fireEvent.click(calendar);
        await fireEvent.click(saveButton);

        await expect(createMenstruationMock).toBeCalledWith(interval);
        await expect(getByTestId(/error/i)).toHaveTextContent(/error/i);
      }));

    it('should show missing selection error', () =>
      act(async () => {
        const { getByText, getByTestId } = render(
          <Create history={historyMock} />
        );
        const saveButton = getByText(/save/i);

        await fireEvent.click(saveButton);

        await expect(createMenstruationMock).not.toBeCalled();
        await expect(getByTestId(/error/i)).toHaveTextContent(/select/i);
      }));
  });
});
