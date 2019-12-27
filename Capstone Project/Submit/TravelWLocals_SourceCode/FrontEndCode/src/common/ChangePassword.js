import React from 'react';
import $ from "jquery";
import Config from '../Config'
import SweetAlert from 'react-bootstrap-sweetalert';
class ChatList extends React.Component {
      constructor(props) {
            super(props);
            this.state = {
                  alert: null
            }
            this.onCancel = this.onCancel.bind(this);
            this.notification = this.notification.bind(this);
            this.onNotification = this.onNotification.bind(this);
            this.statusProfile = this.statusProfile.bind(this);
      }
      componentDidMount() {
            $("head").append('<link href="/css/editPost.css" rel="stylesheet"/>');
      }
      onCancel() {
		this.setState({
			alert: null
		});
	}
	//notification khi booking failed or success
	onNotification() {
		this.setState({ alert: null });
	}

	notification(notification) {
		const getAlert = () => (
			<SweetAlert
				warning
				confirmBtnText="Close"
				confirmBtnBsStyle="danger"
				title="Operation fails"
				onConfirm={() => this.onNotification()}
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
                        title="Done!"
				onConfirm={() => this.onNotification()}
			>
				{message}
			</SweetAlert>
		);

		this.setState({
			alert: getAlert()
		});
	}

      render() {
            let password = null;
            let repassword = null;
            let newpassword = null;
            return (
                  <div className="col-lg-7 push-lg-4 personal-info changePassword">
                        {this.state.alert}
                        <h2 style={{paddingLeft:'10%'}}>Change Password</h2>
                        <form onSubmit={async (e) => {
                              e.preventDefault();
                              if (!password.value.trim()) {
                                    return;
                              }
                              if (!repassword.value.trim()) {
                                    return;
                              }
                              if (!newpassword.value.trim()) {
                                    return;
                              }

                              if (newpassword.value.trim() !== repassword.value.trim()) {
                                    return;
                              }
                              let acc = {
                                    password: password.value.trim(),
                                    rePassword: newpassword.value.trim()
                              };
                              try {
                                    let response = await fetch(Config.api_url + "account/change",
                                          {
                                                method: "POST",
                                                mode: "cors",
                                                credentials: "include",
                                                headers: {
                                                      'Content-Type': 'application/json'
                                                },
                                                body: JSON.stringify(acc)
                                          }
                                    );
                                    if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
                                    //console.log("change password: " + );
                                    response = await response.text()
                                    if (response === 'true') {
                                          this.statusProfile("Your password has been change successful");
                                    } else {
                                          this.notification("Something Wrong! Please try again");
                                    }
                              } catch (err) {
                                    this.notification("Something Wrong! Please try again");
                                    console.log(err);
                              }
                        }}>

                              <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Old password: </label>
                                    <div className="col-lg-8">
                                          <input ref={node => { password = node; }} className="form-control"
                                                required type="password" min="8" min="24" />

                                    </div>
                              </div>
                              <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">New password: </label>
                                    <div className="col-lg-8">
                                          <input ref={node => { newpassword = node; }} required className="form-control" type="password"
                                                min="8" min="24" />

                                    </div>
                              </div>
                              <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Confirm New password:</label>
                                    <div className="col-lg-8">
                                          <input ref={node => { repassword = node; }} className="form-control"
                                                type="password" required min="8" min="24" />
                                    </div>
                              </div>

                              <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label" />
                                    <div className="col-lg-8">

                                          <input
                                                type="submit"
                                                className="btn btn-primary"
                                                value="Change password"
                                          />
                                    </div>
                              </div>
                        </form>
                  </div>
            );
      }
}

export default (ChatList);
