import React from 'react';
import { Route } from 'react-router-dom';

const PublicRoute = ({ component: Component, layout: Layout, ...rest }) => (
  //layout:PublicLayout component:Login ...rest:path='/users/login'
  <Route {...rest} render={props => (
    (
      <Layout>
        <Component {...props} />
      </Layout>
    )
  )} />
);

export default PublicRoute;