import { register, User } from './registerService';
import { createUser, UserDTO } from '../data/registerClient';

jest.mock('../data/registerClient');
const createUserMock = createUser as jest.Mock<Promise<UserDTO>>;

describe('registerService', () => {
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
