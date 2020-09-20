import React from 'react';
import { Route } from 'react-router-dom';
import { RouteProps } from 'react-router';
import { isAuthenticated } from './login/domain/loginService';
import { Login } from './login/presentation/Login';

function redirectToLogin(_: any): JSX.Element {
  return <Login />;
}

export function PrivateRoute(props: Readonly<RouteProps>): JSX.Element {
  const { component, ...rest } = props;
  if (!isAuthenticated()) {
    return <Route {...rest} render={redirectToLogin} />;
  }
  return <Route {...rest} component={component} />;
}

// export function PublicRoute(props: Readonly<RouteProps>): JSX.Element {
//   const { component, ...rest } = props;
//
//   if (isAuthenticated()) {
//     return <Route {...rest} render={(_) => <Redirect to="/" />} />;
//   }
//   return <Route {...rest} component={component} />;
// }
