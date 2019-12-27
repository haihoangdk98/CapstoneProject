import React from 'react';
import { connect } from 'react-redux';
import { send } from '../redux/webSocket';

import Config from '../Config';

class ChatList extends React.Component {
      constructor(props) {
            super(props);
            this.state = {
                  //messages: props.messages,
                  page: 0
            }
      }

      async componentDidMount() {
            try {
                  //console.log(this.props);
                  //if(this.props.receiver === '') return;
                  // let guests = await fetch(`${Config.api_url}messages/${this.props.user.userName}/${this.props.receiver}/${this.state.page}/${this.state.page+10}`, {
                  //       method:"GET",
                  //       mode: "cors",
                  //       credentials: "include",
                  //       headers: {
                  //             'Content-Type': 'application/json',
                  //       },
                  // });
                  // let show = await guests.json();  
                  // console.log(show);
                  //this.setState({messages: show, page: this.state.page+10});
                  //this.setState({messages: this.props.messages});
            } catch(err) {
                  console.log(err);
            }
      }


      render() {
            let input = null;
            let receiver = this.props.receiver;
            let user = this.props.name;
            //let messages = this.props.getState().messages;
            //console.log(this.props);
            return (<div className="ChatRoom" >
                  <div className="top_menu" >
                        <div className="title">Chat</div>
                  </div>
                  <ul className="messages" >
                        {this.props.messages.map((msg, index) => (
                              <li
                                    className={`message ${msg.sender === user ? "right" : "left"} appeared`} key={index}>
                                    <div className="avatar"></div>
                                    <div className="text_wrapper">
                                          {msg.user}
                                          <div className="text">{msg.content}</div>
                                    </div>
                                    <div
                                          style={{ float: "left", clear: "both" }}
                                          ref={el => {
                                                //this.messagesEnd = el;
                                          }}
                                    ></div>
                              </li>
                        ))}
                  </ul>
                  <div className="bottom_wrapper clearfix" >
                        <form onSubmit={e => {
                              e.preventDefault();
                              if (!input.value.trim()) {
                                    return;
                              }
                              //console.log("send 2??");
                              let chatMessage = {
                                    sender: user,
                                    content: input.value,
                                    guider: (this.props.user.role==='GUIDER')?user:receiver,
                                    traveler: (this.props.user.role==='GUIDER')?receiver:user,
                                    type: "CHAT",
                                    isSeen: false,
                                    dateReceived: Date.now()
                              };
                              this.props.dispatch(send(chatMessage));
                              input.value = '';
                              //this.setState({});
                        }}>
                              <div className="message_input_wrapper">
                                    <input ref={node => { input = node; }} className="message_input" placeholder="Type your message here..." />
                              </div>
                              <div className="send_message">
                                    <div className="icon"></div>
                                    <button type="submit" className="text">Send</button>
                              </div>
                              
                        </form>
                  </div>

            </div>);
      }
}
function mapStateToProps(state) {
      const user = state.user;
      const msg = state.messages;
	return { user, msg};
}
export default connect(mapStateToProps)(ChatList);
