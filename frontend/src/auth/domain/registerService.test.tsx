import { confirm, register, User } from './registerService';
import { confirmUser, createUser, UserDTO } from '../data/registerClient';

jest.mock('../data/registerClient');
const createUserMock = createUser as jest.Mock<Promise<UserDTO>>;
const confirmUserMock = confirmUser as jest.Mock<Promise<UserDTO>>;

describe('registerService', () => {
  describe('register', () => {
    it('should return user after successful registration', () => {
      const email = 'foo@bar.com';
      const password = 'password';
      const name = '';
      const createdUser: UserDTO = {
        email,
        password,
        name,
        isVerified: false,
      };
      createUserMock.mockResolvedValue(createdUser);

      const actual = register(email, password, name);
      const expected: User = {
        email: createdUser.email,
        password: createdUser.password,
        name: createdUser.name || '',
        isVerified: createdUser.isVerified,
      };

      return expect(actual).resolves.toStrictEqual(expected);
    });
  });

  describe('confirm', () => {
    it('should confirm given user', () => {
      const id = 'foo123';
      const confirmedUser: UserDTO = {
        email: 'foo@bar.com',
        password: 'password',
        name: 'foo bar',
        isVerified: true,
      };
      confirmUserMock.mockResolvedValue(confirmedUser);

      const actual = confirm(id);
      const expected: User = {
        email: confirmedUser.email,
        password: confirmedUser.password,
        name: confirmedUser.name || '',
        isVerified: confirmedUser.isVerified,
      };

      return expect(actual).resolves.toStrictEqual(expected);
    });
  });
});
