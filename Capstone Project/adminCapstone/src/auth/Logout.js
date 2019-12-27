import React, { Component } from 'react';
import { thisExpression } from '@babel/types';

class Logout extends Component {
    constructor(props){
        super(props);
    }

    componentDidMount() {
        localStorage.clear();
        window.location.href = "/users/login";
        // this.props.history.push('/users/login');
    }

    render() {
        return (null);
    }
}

export default Logout;