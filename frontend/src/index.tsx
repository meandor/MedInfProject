import React from 'react';
import ReactDOM from 'react-dom';
import './index.scss';
import { HashRouter as Router, Link } from 'react-router-dom';
import * as serviceWorker from './serviceWorker';
import { PrivateRoute, PublicRoute } from './Routes';
import { Login } from './login/presentation/Login';

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <header>
        <Link className="logo" to="/">
          Menstra
        </Link>
      </header>
      <main>
        <PrivateRoute path="/" exact component={() => <></>} />
        <PublicRoute path="/login" exact component={Login} />
      </main>
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);

serviceWorker.unregister();
