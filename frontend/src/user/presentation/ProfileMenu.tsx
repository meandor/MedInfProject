import React, { useState } from 'react';
import {
  faCaretDown,
  faEnvelope,
  faCheckCircle,
  faSignOutAlt,
  faUserTimes,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import {
  authenticatedUser,
  IDToken,
  signOutUser,
} from '../domain/loginService';
import './profileMenu.scss';

function renderEmailVerifiedSign(idToken: IDToken): JSX.Element {
  if (!idToken.email_verified) {
    return <></>;
  }
  return (
    <FontAwesomeIcon
      icon={faCheckCircle}
      title="email is verified"
      className="profile_menu__username__verified"
    />
  );
}

function infoClassName(isShowing: boolean): string {
  if (isShowing) {
    return 'profile_menu__info';
  }
  return 'profile_menu__info-hidden';
}

function usernameClassName(isShowing: boolean): string {
  if (isShowing) {
    return 'profile_menu__username-selected';
  }
  return 'profile_menu__username';
}

export function ProfileMenu(_props: any): JSX.Element {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const idToken = authenticatedUser();
  const logout: () => void = () => {
    signOutUser();
    window.location.href = '/';
  };

  if (idToken === undefined) {
    return <></>;
  }
  return (
    <section className="profile_menu">
      <section
        className={usernameClassName(isOpen)}
        onClick={() => setIsOpen(!isOpen)}
        aria-hidden="true"
      >
        {idToken.name || idToken.email} {renderEmailVerifiedSign(idToken)}{' '}
        <FontAwesomeIcon icon={faCaretDown} />
      </section>
      <section className={infoClassName(isOpen)}>
        <p className="profile_menu__info__item">
          <FontAwesomeIcon icon={faEnvelope} className="right_space" />{' '}
          {idToken.email}
        </p>
        <hr />
        <Link to="/profile/delete">
          <FontAwesomeIcon icon={faUserTimes} className="right_space" /> Delete
          Account
        </Link>
        <hr />
        <p className="profile_menu__info__actions">
          <button
            type="submit"
            className="button button-primary"
            onClick={logout}
          >
            <FontAwesomeIcon icon={faSignOutAlt} /> Logout
          </button>
        </p>
      </section>
    </section>
  );
}
