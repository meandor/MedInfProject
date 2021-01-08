import { get, MenstruationDTO, post } from '../data/menstruationClient';
import { createMenstruation, find } from './menstruationService';
import { authenticatedUser, IDToken } from '../../user/domain/loginService';

jest.mock('../../user/domain/loginService');
jest.mock('../data/menstruationClient');

const authenticatedUserMock = authenticatedUser as jest.Mock<
  IDToken | undefined
>;
const postMock = post as jest.Mock<Promise<MenstruationDTO>>;
const getMock = get as jest.Mock<Promise<MenstruationDTO[]>>;

describe('createMenstruation', () => {
  it('should take a menstruation and create it', () => {
    authenticatedUserMock.mockReturnValue({
      name: 'foo bar',
      email: 'foo@bar.com',
      email_verified: true,
      sub: 'foo-bar-000',
    });
    const createdMenstruation = {
      start: '2020-01-01',
      end: '2020-01-01',
    };
    postMock.mockResolvedValue(createdMenstruation);
    const menstruation = { start: new Date(), end: new Date() };

    const actual = createMenstruation(menstruation);
    const expected = {
      start: new Date(2020, 0, 1),
      end: new Date(2020, 0, 1),
    };

    expect(postMock).toHaveBeenCalled();
    return expect(actual).resolves.toStrictEqual(expected);
  });

  it('should reject for not authenticated user', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    const menstruation = { start: new Date(), end: new Date() };

    const actual = createMenstruation(menstruation);

    return expect(actual).rejects.toBeTruthy();
  });
});

describe('find', () => {
  it('should find all menstruation', () => {
    authenticatedUserMock.mockReturnValue({
      name: 'foo bar',
      email: 'foo@bar.com',
      email_verified: true,
      sub: 'foo-bar-000',
    });
    const menstruation: MenstruationDTO = {
      start: '2020-01-01',
      end: '2020-01-05',
    };
    getMock.mockResolvedValue([menstruation]);

    const actual = find();
    const expected = [
      {
        start: new Date(2020, 0, 1),
        end: new Date(2020, 0, 5),
      },
    ];

    expect(getMock).toHaveBeenCalled();
    return expect(actual).resolves.toStrictEqual(expected);
  });

  it('should reject for not authenticated user', () => {
    authenticatedUserMock.mockReturnValue(undefined);

    const actual = find();

    return expect(actual).rejects.toBeTruthy();
  });
});
