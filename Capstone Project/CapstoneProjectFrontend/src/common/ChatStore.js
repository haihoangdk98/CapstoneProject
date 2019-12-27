import React from 'react';
import { connect } from 'react-redux';
import { loadMsg, send } from '../redux/webSocket';
import $ from 'jquery';
import Config from '../Config';

class ChatList extends React.Component {
      constructor(props) {
            super(props);
      }


      componentDidMount() {
            $('.messages').scrollTop($('.messages')[0].scrollHeight);
      }


      render() {
            let input = null;
            let receiver = this.props.receiver;
            let user = this.props.name;
            //let messages = this.props.getState().messages;
            //console.log(this.props);
            return (<div className="ChatRoom" >
                  <div className="top_menu" >
                        <div className="title" style={{ color: 'Black' }}>{receiver}</div>
                  </div>
                  <div className={`box_noti`} style={{ textAlign: 'center', cursor: 'pointer' }}>
                        <a className="showMore" onClick={() => { this.props.dispatch(loadMsg(`${this.props.user.userName}/${this.props.receiver}/${this.props.messages.length}/${this.props.messages.length + 10}`)); }} >Show more</a>
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
                  <div
                        ref={(el) => { this.messagesEnd = el; }}>
                  </div>
                  <div className="bottom_wrapper clearfix" >
                        <form onSubmit={e => {
                              e.preventDefault();
                              if (!input.value.trim() || !receiver.trim()) {
                                    return;
                              }
                              //console.log("send 2??");
                              let chatMessage = {
                                    sender: user,
                                    content: input.value,
                                    guider: (this.props.user.role === 'GUIDER') ? user : receiver,
                                    traveler: (this.props.user.role === 'GUIDER') ? receiver : user,
                                    type: "CHAT",
                                    isSeen: false,
                                    dateReceived: Date.now()
                              };
                              console.log(chatMessage);
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
      return { user, msg };
}
export default connect(mapStateToProps)(ChatList);
