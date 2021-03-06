import React, { Component } from "react";
import $ from "jquery";
import "font-awesome/css/font-awesome.min.css";
import { BrowserRouter as Router, Switch, Route, Link, Redirect } from "react-router-dom";
import Config from "../Config";
import { connect } from 'react-redux';
import { exit, logOut } from '../redux/actions';
import { wsConnect, wsDisconnect, send } from '../redux/webSocket';

class LoggedTvl extends Component {
  constructor(props) {
    super(props);
    this.state = {
      disable: true,
      avatar: ''
    };
  }

  async componentDidMount() {
    $("head").append('<link href="/css/login.css" rel="stylesheet"/>');
    $("head").append('<link href="/css/navbar.css" rel="stylesheet"/>');
    $(".button-group > button").on("click", function () {
      $(".button-group > button").removeClass("active");
      $(this).addClass("active");
    });

    let pathname = window.location.pathname;
    if (pathname !== "/") {
      $("input[name=search]").focus(function () {
        $(".search .fillter").show();
      });
      $(document).mouseup(function (e) {
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

    $(document).mouseup(function (e) {
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
    console.log(dataTraveller);
    this.setState({ avatar: dataTraveller.avatar_link });
  }

  disableLoggedChoice = () => {
    this.setState({ disable: !this.state.disable });
  };

  render() {
    var guider_id = JSON.parse(sessionStorage.getItem("guider_id"));
    let { avatar } = this.state;
    let avatar_link;
    if (avatar === "") {
      avatar_link = <img src={`${Config.api_url}images/account.jpg`} />
    } else {
      avatar_link = <img src={`${avatar}`} />
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
                  </a>
                </div>
                <div className="search">
                  <label>
                    <input
                      type="text"
                      placeholder="Welcome to my website"
                      name="search"
                      autoComplete="off"
                    />
                  </label>
                  <div className="fillter">
                    <div className="filter-Content">
                      <div className="localsOrExperience">
                        <h3 className="explore">Explore TravelWlocals</h3>
                        <div className="button-group">
                          <button className="active">All</button>
                          <button>Locals</button>
                          <button>Experiences</button>
                        </div>
                      </div>
                      <div className="popularDestination">
                        <h3 id="popularLabel">Popular Destinations</h3>
                        <ul>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Ha Noi</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Ho Chi Minh</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Da Nang</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Bac Ninh</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Da Lat</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Hue</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Vung Tau</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Hai Phong</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Phu Quoc</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Sapa</a>
                          </li>
                          <li>
                            <i className="fa fa-map-marker" aria-hidden="true"></i>
                            <a>Ca Mau</a>
                          </li>
                        </ul>
                      </div>
                    </div>

                  </div>
                </div>
              </div>
              <div className="navRight">
                <div className="navbarRightContent">
                  <ul className="logged">
                    <li>
                      <Link to="/"><i className="fa fa-users" aria-hidden="true"></i></Link>
                    </li>
                    <li>
                      <Link to="/chat"><i className="fa fa-comments" aria-hidden="true"></i></Link>
                    </li>
                    <li>
                      <Link to="/"><i className="fa fa-bell" aria-hidden="true"></i></Link>
                    </li>
                    <li>
                      <Link to={"/tvlManager"}>Bookings</Link>
                    </li>
                    <li className="avatarLogged" onClick={this.disableLoggedChoice}>
                      {
                        avatar_link
                      }
                      <ul
                        className="dropContent"
                        style={
                          this.state.disable
                            ? { display: "none" }
                            : { display: "block" }
                        }
                      >
                        <span>
                          <li>
                            <Link to="/profiletraveller">Profile traveller</Link>
                            <i
                              className="fa fa-address-card-o"
                              aria-hidden="true"
                            ></i>
                          </li>
                          <li>
                            <Link to="/tvlManager">Traveler manage</Link>
                            <i className="fa fa-suitcase" aria-hidden="true"></i>
                          </li>
                          <li>
                            <Link to="/changepassword">Change password</Link>
                            <i class="fa fa-key" aria-hidden="true"></i>
                          </li>
                        </span>
                        <li
                          onClick={() => {
                            // console.log("log out");
                            // const user = {
                            //   userName: "Guest",
                            //   role: "GUEST",
                            //   id: 0
                            // };
                            // this.props.reload.call(this, user);window.location.href = "/";
                            this.props.dispatch(logOut());
                            this.props.dispatch(wsDisconnect(Config.api_url + "ws"));
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
      </div >
    );
  }
}
function mapStateToProps(state) {
  const user = state.user;
  return { user };
}
export default connect(mapStateToProps)(LoggedTvl);
