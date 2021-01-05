import { postMenstruation, MenstruationDTO } from '../data/menstruationClient';
import { createMenstruation } from './menstruationService';
import { authenticatedUser, IDToken } from '../../auth/domain/loginService';

jest.mock('../../auth/domain/loginService');
jest.mock('../data/menstruationClient');

const authenticatedUserMock = authenticatedUser as jest.Mock<
  IDToken | undefined
>;
const postMenstruationMock = postMenstruation as jest.Mock<
  Promise<MenstruationDTO>
>;

describe('menstruationService', () => {
  it('should take a menstruation and create it', () => {
    authenticatedUserMock.mockReturnValue({
      name: 'foo bar',
      email: 'foo@bar.com',
      email_verified: true,
      sub: 'foo-bar-000',
    });
    const createdMenstruation = {
      start: '2020-01-01T00:00:00.000Z',
      end: '2020-01-01T00:00:00.000Z',
    };
    postMenstruationMock.mockResolvedValue(createdMenstruation);
    const menstruation = { start: new Date(), end: new Date() };

    const actual = createMenstruation(menstruation);
    const expected = {
      start: new Date(Date.UTC(2020, 0, 1)),
      end: new Date(Date.UTC(2020, 0, 1)),
    };

    expect(postMenstruationMock).toHaveBeenCalled();
    return expect(actual).resolves.toStrictEqual(expected);
  });

  it('should reject for not authenticated user', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    const menstruation = { start: new Date(), end: new Date() };

    const actual = createMenstruation(menstruation);

    return expect(actual).rejects.toBeTruthy();
  });
});
