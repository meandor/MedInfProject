import { deleteData, deleteAccount, unlinkData } from './userService';
import {
  anonymizeData,
  deleteUserData,
  deleteUserAccount,
} from '../data/deleteClient';
import { authenticatedUser, IDToken } from './loginService';

jest.mock('../data/deleteClient');
jest.mock('./loginService');

const anonymizeDataMock = anonymizeData as jest.Mock<Promise<void>>;
const deleteUserDataMock = deleteUserData as jest.Mock<Promise<void>>;
const deleteUserAccountMock = deleteUserAccount as jest.Mock<Promise<void>>;
const authenticatedUserMock = authenticatedUser as jest.Mock<
  IDToken | undefined
>;

describe('unlinkData', () => {
  it('should get user and call anonymizeData', () => {
    authenticatedUserMock.mockReturnValue({
      email: 'foo@bar.com',
      email_verified: true,
      name: 'foo bar',
      sub: 'fooobar',
    });
    anonymizeDataMock.mockResolvedValue();

    const actual = unlinkData();

    return expect(actual).resolves.toBeUndefined();
  });

  it('should reject when user not logged in', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    anonymizeDataMock.mockResolvedValue();

    const actual = unlinkData();

    return expect(actual).rejects.toBeTruthy();
  });
});

describe('deleteData', () => {
  it('should get user and call deleteUserData', () => {
    authenticatedUserMock.mockReturnValue({
      email: 'foo@bar.com',
      email_verified: true,
      name: 'foo bar',
      sub: 'fooobar',
    });
    deleteUserDataMock.mockResolvedValue();

    const actual = deleteData();

    return expect(actual).resolves.toBeUndefined();
  });

  it('should reject when user not logged in', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    deleteUserDataMock.mockResolvedValue();

    const actual = deleteData();

    return expect(actual).rejects.toBeTruthy();
  });
});

describe('deleteAccount', () => {
  it('should get user and call deleteUserAccount', () => {
    authenticatedUserMock.mockReturnValue({
      email: 'foo@bar.com',
      email_verified: true,
      name: 'foo bar',
      sub: 'fooobar',
    });
    deleteUserAccountMock.mockResolvedValue();

    const actual = deleteAccount();

    return expect(actual).resolves.toBeUndefined();
  });

  it('should reject when user not logged in', () => {
    authenticatedUserMock.mockReturnValue(undefined);
    deleteUserAccountMock.mockResolvedValue();

    const actual = deleteAccount();

    return expect(actual).rejects.toBeTruthy();
  });
});
