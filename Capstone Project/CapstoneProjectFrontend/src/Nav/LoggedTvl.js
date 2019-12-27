import React, { Component } from "react";
import $ from "jquery";
import "font-awesome/css/font-awesome.min.css";
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link,
  Redirect
} from "react-router-dom";
import Config from "../Config";
import { connect } from "react-redux";
import { exit, logOut } from "../redux/actions";
import { wsConnect, wsDisconnect, send , loadNoti} from "../redux/webSocket";

class LoggedTvl extends Component {
  constructor(props) {
    super(props);
    this.state = {
      disable: true,
      avatar: ""
    };
  }

  async componentDidMount() {
    $("head").append('<link href="/css/login.css" rel="stylesheet"/>');
    $("head").append('<link href="/css/navbar.css" rel="stylesheet"/>');
    $(".button-group > button").on("click", function() {
      $(".button-group > button").removeClass("active");
      $(this).addClass("active");
    });

    let pathname = window.location.pathname;
    if (pathname !== "/") {
      $("input[name=search]").focus(function() {
        $(".search .fillter").show();
      });
      $(document).mouseup(function(e) {
        if (
          !$(".search").is(e.target) &&
          !$(".fillter").is(e.target) &&
          $(".search").has(e.target).length === 0 &&
          $(".fillter").has(e.target).length === 0
        ) {
          $(".fillter").hide();
        }
      });
    }

    $(document).mouseup(function(e) {
      var container = $(".dropContent");
      // if the target of the click isn't the container nor a descendant of the container
      if (!container.is(e.target) && container.has(e.target).length === 0) {
        $(".dropContent").hide();
      }
    });

    const responseTraveller = await fetch(
      Config.api_url + "Traveler/GetTraveler?traveler_id=" + this.props.user.id,
      {
        method: "GET",
        mode: "cors",
        credentials: "include"
      }
    );

    const dataTraveller = await responseTraveller.json();
    this.setState({ avatar: dataTraveller.avatar_link });
  }

  disableLoggedChoice = () => {
    this.setState({ disable: !this.state.disable });
  };

  toggleBox = () => {
    this.setState(prevState => ({ isBoxVisible: !prevState.isBoxVisible }));
};

  render() {
    var guider_id = JSON.parse(sessionStorage.getItem("guider_id"));
    let { avatar } = this.state;
    const { isBoxVisible } = this.state;
    let notis = this.props.noti.map((no, index) => <li key={index}><a >{no.content}</a></li>);
    let avatar_link;
    if (avatar === "") {
      avatar_link = <img src={`${Config.api_url}images/account.jpg`} />;
    } else {
      avatar_link = <img src={`${avatar}`} />;
    }

    return (
      <div>
        {/* Menubar */}

        <nav className="navbar">
          <div className="menubar">
            <div className="containerMain">
              <div className="navLeft">
                <div className="logoContainer">
                  <a href="/">
                    <img src="/icon/iconMain.jpg" />
                    <h3>Enjoy a city like a local</h3>
                  </a>
                  
                </div>
              </div>
              <div className="navRight">
                <div className="navbarRightContent">
                  <ul className="logged">
                    {/* <li>
                      <Link to="/"><i className="fa fa-users" aria-hidden="true"></i></Link>
                    </li> */}
                    <li>
                      <Link to="/chat">
                        <i className="fa fa-comments" aria-hidden="true"></i>
                      </Link>
                    </li>
                    <div className="noti">
                      <li>
                        <a onClick={this.toggleBox}>
                          <i className="fa fa-bell" aria-hidden="true"></i>
                        </a>
                      </li>
                      <div
                        className={`box_noti ${isBoxVisible ? "" : "hidden"}`}
                      >
                        <ul className="list_noti">
                          {/* <li><a ><span>Canceled </span>The order on tour "Vietnamese war" was canceled by traveler Dung Nguyen</a></li> */}
                          {notis}
                        </ul>

                        <a
                          className="showMore"
                          onClick={() => {
                            this.props.dispatch(
                              loadNoti(
                                `${this.props.user.userName}/${
                                  this.props.noti.length
                                }/${this.props.noti.length + 5}`
                              )
                            );
                          }}
                        >
                          Show more
                        </a>
                      </div>
                    </div>
                    <li
                      className="avatarLogged"
                      onClick={this.disableLoggedChoice}
                    >
                      {avatar_link}
                      <ul
                        className="dropContent"
                        style={
                          this.state.disable
                            ? { display: "none" }
                            : { display: "block" }
                        }
                      >
                        <span>
                          <li className="userChoice">
                            <Link to="/profiletraveller">
                              Profile
                            </Link>
                            <i
                              className="fa fa-address-card-o"
                              aria-hidden="true"
                            ></i>
                          </li>
                          <li className="userChoice">
                            <Link to="/tvlManager">Bookings manage</Link>
                            <i
                              className="fa fa-suitcase"
                              aria-hidden="true"
                            ></i>
                          </li>
                          <li className="userChoice">
                            <Link to="/favorite">Your favorite</Link>
                            <i
                              className="fa fa-suitcase"
                              aria-hidden="true"
                            ></i>
                          </li>
                          <li className="userChoice">
                            <Link to="/changepassword">Change password</Link>
                            <i className="fa fa-key" aria-hidden="true"></i>
                          </li>
                        </span>
                        <li
                          className="userChoice"
                          onClick={() => {
                            // console.log("log out");
                            // const user = {
                            //   userName: "Guest",
                            //   role: "GUEST",
                            //   id: 0
                            // };
                            // this.props.reload.call(this, user);window.location.href = "/";
                            this.props.dispatch(logOut());
                            this.props.dispatch(
                              wsDisconnect(Config.api_url + "ws")
                            );
                            this.props.dispatch(exit());
                            //window.location.href = "/";
                          }}
                        >
                          Log out
                          <i className="fa fa-sign-out" aria-hidden="true"></i>
                        </li>
                      </ul>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </nav>
        {/* End MenuBar */}
      </div>
    );
  }
}
function mapStateToProps(state) {

  const user = state.user;
  const noti = state.notifications;
  return { user,noti };
}
export default connect(mapStateToProps)(LoggedTvl);
