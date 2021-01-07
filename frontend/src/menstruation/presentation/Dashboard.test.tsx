import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { act, render } from '@testing-library/react';
import { Dashboard } from './Dashboard';
import { Event, predict, Prediction } from '../domain/predictionService';
import { Menstruation, find } from '../domain/menstruationService';

jest.mock('../domain/predictionService');
jest.mock('../domain/menstruationService');

const predictMock = predict as jest.Mock<Promise<Prediction>>;
const findMock = find as jest.Mock<Promise<Menstruation[]>>;

describe('Dashboard component', () => {
  const historyMock = {
    push: jest.fn(),
  };

  describe('calendar component', () => {
    let calendar: HTMLElement;

    it('should render empty calendar', async () => {
      findMock.mockResolvedValue([]);
      predictMock.mockResolvedValue({
        event: Event.OVULATION,
        isUpcoming: true,
        days: 1,
      });

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        calendar = getByTestId(/calendar/i);
      });

      await expect(calendar).toBeInTheDocument();
    });
  });

  describe('prediction component', () => {
    let predictionField: HTMLElement;
    const today = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);
    const in20Days = new Date();
    in20Days.setDate(today.getDate() + 20);
    const in21Days = new Date();
    in21Days.setDate(today.getDate() + 21);

    it('should render ovulation in one day', async () => {
      predictMock.mockResolvedValue({
        event: Event.OVULATION,
        isUpcoming: true,
        days: 1,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/1 day/i);
      await expect(predictionField).toHaveTextContent(/ovulation/i);
    });

    it('should render ovulation in multiple days', async () => {
      predictMock.mockResolvedValue({
        event: Event.OVULATION,
        isUpcoming: true,
        days: 42,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/42 days/i);
      await expect(predictionField).toHaveTextContent(/ovulation/i);
    });

    it('should render period in one day', async () => {
      predictMock.mockResolvedValue({
        event: Event.MENSTRUATION,
        isUpcoming: true,
        days: 1,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/1 day/i);
      await expect(predictionField).toHaveTextContent(/period/i);
    });

    it('should render period in multiple days', async () => {
      predictMock.mockResolvedValue({
        event: Event.MENSTRUATION,
        isUpcoming: true,
        days: 13,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/13 days/i);
      await expect(predictionField).toHaveTextContent(/period/i);
    });

    it('should render period and n days left', async () => {
      predictMock.mockResolvedValue({
        event: Event.MENSTRUATION,
        isUpcoming: false,
        days: 3,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/3 days/i);
      await expect(predictionField).toHaveTextContent(/left/i);
      await expect(predictionField).toHaveTextContent(/period/i);
    });

    it('should render ovulation today', async () => {
      predictMock.mockResolvedValue({
        event: Event.OVULATION,
        isUpcoming: false,
        days: 0,
      });
      findMock.mockResolvedValue([]);

      await act(async () => {
        const { getByTestId } = render(<Dashboard history={historyMock} />);
        predictionField = getByTestId(/prediction-field/i);
      });

      await expect(predictionField).toHaveTextContent(/today/i);
      await expect(predictionField).toHaveTextContent(/ovulation/i);
    });
  });
});
