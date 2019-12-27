import React, { Component } from 'react';
export default class PublicLayout extends Component {
    render() {
        return  (
            <div id="wrapper" className="adminLogin">
                {this.props.children}
            </div>
        );
    }
}