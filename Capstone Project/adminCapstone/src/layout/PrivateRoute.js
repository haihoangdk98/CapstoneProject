import React from 'react';
import { Redirect, Route } from 'react-router-dom';

const PrivateRoute = ({ component: Component, layout: Layout, ...rest }) => (
  <Route {...rest} render={props => (
      (localStorage.getItem('isAuthenticate')) ? (
        <Layout>
          <Component {...props} />
        </Layout>
      ) : (
        <Redirect to={{
          pathname: '/users/login',
          state: { from: props.location }
          }}
        />
      )
  )} />
);

export default PrivateRoute;