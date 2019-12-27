import React, { Component } from 'react';
import SweetAlert from 'react-bootstrap-sweetalert';
import Config from '../Config';
import $ from 'jquery';
class ForgotPassword extends Component {
    constructor(props){
        super(props);
        this.state = {
            alert:null,
            username:''
        }
    }

    handleSubmit = async ()=>{
        $('.coverLoader').show();
        try {
            const response = await fetch(
              Config.api_url + "account/forgotPasswordConfirm?username=" + this.state.username,
              {
                method: "GET",
                mode: "cors",
                credentials: "include",
                headers: {
                  Accept: "application/json",
                  
                }
              }
            );
            const result = await response.text();
            if(!response.ok){
                this.notification("Account doesn't not exist!!");
            }
            $('.coverLoader').hide();
            if(result === 'An email has been sent to you'){
                this.statusProfile(result);
            }else if(result === 'Your email was not verified'){
                this.notification(result);
            }
            
            this.setState({username:''})
          } catch (err) {
            console.log(err);
          }
        
    }

    handleChange = (e) =>{
        let value = e.target.value;
        this.setState({username:value});
    }

    hideAlert() {
        this.setState({
            alert: null
        });
    }

    notification(notification) {
		const getAlert = () => (
			<SweetAlert
				warning
				confirmBtnText="Close"
				confirmBtnBsStyle="danger"
				title="Notification"
				onConfirm={() => this.hideAlert()}
			>
				{notification}
			</SweetAlert>
		);

		this.setState({
			alert: getAlert()
		});
    }
    
    statusProfile(message) {
        const getAlert = () => (
            <SweetAlert
                success
                onConfirm={() => this.hideAlert()}
            >
                {message}
            </SweetAlert>
        );

        this.setState({
            alert: getAlert()
        });
    }
    render() {
        return (
            <div>
            {this.state.alert}
            <div className="coverLoader">
                <div className="loader"></div>
            </div>
                <div className="forgotPassword">
                <h3>Enter your username to get password</h3>
                <div className="form-group row" >
                <div className="col-lg-3 group_IncludeSer">
                    <label className="col-form-label form-control-label" id="forgotEmail">
                    Username
                    </label>
                  
                </div>
                <div className="col-lg-7 include-service">
                    <div className="dropdownCoverSelect inputUserNamePassword">
                    <input
                        className="dropdown-select service"
                        type="text"
                        required
                        name="service"
                        onChange={this.handleChange}
                        value={this.state.username}
                    />
                    </div>
                </div>
                <div className="col-lg-2 group_IncludeSer">
                    <div className="submit_btn" style={{marginTop:'0'}}><button className="submitBtn btnSb" onClick={this.handleSubmit}>Send</button></div>
                </div>
                </div>
            </div>
            </div>
        );
    }
}

export default ForgotPassword;