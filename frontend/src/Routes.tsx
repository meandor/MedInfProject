import React from 'react';
import { Redirect, Route } from 'react-router-dom';
import { RouteProps } from 'react-router';
import { isAuthenticated } from './auth/domain/loginService';

export function PrivateRoute(props: Readonly<RouteProps>): JSX.Element {
  const { component, ...rest } = props;
  if (!isAuthenticated()) {
    return <Route {...rest} render={(_) => <Redirect to="/login" />} />;
  }
  return <Route {...rest} component={component} />;
}

export function PublicRoute(props: Readonly<RouteProps>): JSX.Element {
  const { component, ...rest } = props;

  if (isAuthenticated()) {
    return <Route {...rest} render={(_) => <Redirect to="/" />} />;
  }
  return <Route {...rest} component={component} />;
}
