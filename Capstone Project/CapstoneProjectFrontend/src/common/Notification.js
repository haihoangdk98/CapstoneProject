import React, { Component } from "react";
import {Link} from 'react-router-dom';
export default class Notification extends Component {

    constructor(props) {
        super(props);
    }

    render() {
        let { isError, message } = this.props;
        if(!message) 
            return null;

        return (
            <h3 className={`alert alert-${isError ? 'danger' : 'success'}`}>{message}</h3>
        );
    }

}