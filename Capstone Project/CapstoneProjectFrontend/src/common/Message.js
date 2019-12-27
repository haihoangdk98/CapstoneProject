import React from 'react';
import { connect } from 'react-redux';
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import ChatList from "./ChatStore";
import Config from '../Config'
import { loadGuest, wsConnect } from '../redux/webSocket'
class Message extends React.Component {
      constructor(props) {
            super(props);
            this.state = {
                  client: "",
                  page: 0
            }
      }

      async componentDidMount() {
            //this.props.dispatch(wsConnect(Config.api_url + "ws"));
            this.props.dispatch(loadGuest(this.props.user ,`${this.props.user.userName}/${this.state.page}/${this.state.page + 5}`));
            this.setState({ page: this.state.page + 5 });
      }

      load = (eve) => {
            eve.preventDefault();
            //console.log(eve.target);
            this.setState({ client: eve.target.id });
      }

      render() {
            //let messages = this.props.getState().messages;
            //console.log(this.props);
            let countUser = this.props.clients;
            return (
                  <div className="messageRoom">
                        <div className="chat_window">
                              <div className="plan">
                                    <div className="planContent">
                                          <h1>Chat</h1>
                                          <div style={{ marginBottom: "30px" }} />
                                          <ul style={{ listStyleType: 'none', }}>
                                                {
                                                      this.props.clients.filter(client => client !== this.props.user.userName && client !== '').map((value, index) => (
                                                            <li key={index} id="liUserChat">
                                                                  <a onClick={this.load} style={{ color: '#385898', cursor: 'pointer', width: '100%', paddingLeft: '8px', paddingRight: '8px', borderRadius: "8px" }}>
                                                                        <div className="detail" >
                                                                              <p id={value} style={
                                                                                    {color:'rgb(80, 80, 80)',fontSize:'25px',marginBottom: '0'}}>
                                                                                    {value}
                                                                              </p>
                                                                              <p style={{color:'rgb(196, 192, 192)'}}>Click to read full messages ...</p>
                                                                        </div>
                                                                       
                                                                  </a>
                                                            </li>
                                                      ))
                                                }
                                          </ul>
                                    </div>
                                    {
                                          countUser.length < 5 ? '' :
                                          <div className={`box_noti`} id="showMoreChat">
                                          <a className="showMore" onClick={() => { this.props.dispatch(loadGuest(this.props.user,`${this.props.user.userName}/${this.props.clients.length}/${this.props.clients.length + 5}`)) }} >Show more</a>
                                    </div>
                                    }
                              </div>
                              <ChatList name={this.props.user.userName} receiver={this.state.client}
                                    messages={this.props.messages
                                          .filter(msg => this.props.user.role === 'GUIDER'
                                                ? (msg.guider === this.props.user.userName && msg.traveler === this.state.client)
                                                : (msg.guider === this.state.client && msg.traveler === this.props.user.userName))
                                    } />

                        </div>
                  </div>);
      }
}
function mapStateToProps(state) {
      //console.log(state);
      const messages = state.messages;
      const clients = state.clients;
      const user = state.user;
      return { messages, clients, user };
}
export default connect(mapStateToProps)(Message);
