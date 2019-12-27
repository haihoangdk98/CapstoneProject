import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import country from "../json/country.json";
import $ from "jquery";
import Config from "../Config";
import ReviewInPost from '../Guider/ReviewInPost';
import GuiderInPost from '../Guider/GuiderInPost';
import { connect } from 'react-redux';

class Pay extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tourDetail: {},
      country: [],
      postInfo: {
        picture_link:['']
      },
      isDisabledPay: true,
      isDisabledCheckBox: true,
      link_youtube: '',
      errors: [],
      isError: false,
      data: {
        traveler_id: '',
        first_name: '',
        last_name: '',
        country_phone: '+84',
        phone: ''
      }
    };
  }

  async componentDidMount() {
    this.setState({ country });
    //open loader to paypal


    $("#amout_show").hover(function () {
      $('.tool_tipPeople').show();
    }, function () {
      $('.tool_tipPeople').hide();
    })

    $('.guiderNamePay').click(function () {
      if ($('.guiderNamePay').hasClass('showing') === false) {
        $('.tool_tipGuider').show();
        $('.guiderNamePay').addClass('showing');
      } else {
        $('.tool_tipGuider').hide();
        $('.guiderNamePay').removeClass('showing');
      }
    });

    $(document).mouseup(function (e) {
      var container = $(".tool_tipGuider");
      // if the target of the click isn't the container nor a descendant of the container
      if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.guiderNamePay').is(e.target)) {
        $('.tool_tipGuider').hide();
        $('.guiderNamePay').removeClass('showing');
      }

    });

    $('.titleTourPay').click(function () {
      if ($('.titleTourPay').hasClass('showing') === false) {
        $('.tool-tipPost').show();
        $('.titleTourPay').addClass('showing');
      } else {
        $('.tool-tipPost').hide();
        $('.titleTourPay').removeClass('showing');
      }
    });

    $(document).mouseup(function (e) {
      var container = $(".tool-tipPost");
      // if the target of the click isn't the container nor a descendant of the container
      if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.titleTourPay').is(e.target)) {
        $('.tool-tipPost').hide();
        $('.titleTourPay').removeClass('showing');
      }

    });

    //load post data
    let tourDetail = JSON.parse(sessionStorage.getItem('tourDetail'));
    let response = await fetch(
      Config.api_url + "guiderpost/findSpecificPost?post_id=" + tourDetail.post_id,
      {
        method: "GET",
        mode: "cors",
        credentials: "include"
      }
    );
    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }

    const postInfo = await response.json();

    let link_youtube = postInfo.video_link;
    if (link_youtube.includes('youtu.be')) {
      link_youtube = link_youtube.replace("youtu.be", "youtube.com/embed");
      this.setState({ link_youtube });
    } else {
      link_youtube = link_youtube.split("&");
      this.setState({ link_youtube: link_youtube[0].replace("watch?v=", "embed/") });
    }
    this.setState({ postInfo });

    //check infomation travel have exist
    const res = await fetch(
      Config.api_url + "Traveler/isLackingProfile?traveler_id=" + this.props.user.id, {
      method: "GET",
      mode: "cors",
      credentials: "include"
    });
    const result = await res.json();
    if (result === true) {
      this.setState({ isDisabledPay: true });
    } else {
      this.setState({ isDisabledPay: false });
    }
  }

  async goToPayPal() {
    var data = JSON.parse(sessionStorage.getItem("tourDetail"));
    delete data.price;
    delete data.dateForBook;
    delete data.hourForBook;
    $(".coverLoader").show();
    let options = {
      method: "POST",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
    let response = await fetch(Config.api_url + "Payment/Pay", options);
    response = await response.text();
    window.location.href = response;
    sessionStorage.setItem("link", response);
  }



  validatePhone(phone) {
    const pattern = /^\d{10,11}$/;
    const result = pattern.test(phone);
    return result;
  }

  handleCheckBox = () => {
    this.setState({ isDisabledCheckBox: !this.state.isDisabledCheckBox });
  }

  handleChange(e) {
    const value = e.target.value;
    const name = e.target.name;
    let { errors } = this.state;
    const { data } = this.state;
    data[name] = value;
    if (value !== '') {
      errors[name] = '';
    }
    this.setState({ data });
  }

  isValidate = () => {
    const { data } = this.state;
    let isError = false;
    let errors = {};
    if (data.first_name === '') {
      isError = true;
      errors['first_name'] = 'First name is empty, Input your first name';
    }
    if (data.last_name === '') {
      isError = true;
      errors['last_name'] = 'Last name is empty, Input your last name';
    }
    if (this.validatePhone(data.phone) === false) {
      isError = true;
      errors['phone'] = 'Phone must be digits and have 10-11 digits';
    }

    this.setState({ isError, errors });
    if (isError)
      return true;

    return false;
  }

  submitForm = async e => {
    e.preventDefault();
    let { data } = this.state;
    if (this.isValidate()) {
      return false;
    }
    // let user = JSON.parse(window.sessionStorage.getItem('user'));
    data.traveler_id = this.props.user.id;
    try {
      const response = await fetch(Config.api_url + "Traveler/updateLackingProfile", {
        method: "POST",
        mode: "cors",
        credentials: "include",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      });
      // if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
      const guider = await response.json();
    } catch (err) {
      console.log(err);
    }
    this.setState({ isDisabledPay: false });
  };

  render() {
    let country_name = this.state.country.map((value, index) => {
      return (
        <option key={index} value={value.name}>
          {value.name + "(" + value.code + ")"}
        </option>
      );
    });

    let country_phone = this.state.country.map((value, index) => {
      return (
        <option key={index} value={value.dial_code}>
          {value.name + "(" + value.dial_code + ")"}
        </option>
      );
    });
    let { postInfo } = this.state;
    console.log("postInfo", postInfo);
    let tourDetail = JSON.parse(sessionStorage.getItem("tourDetail"));

    // let user = JSON.parse(sessionStorage.getItem("user"));
    // if (tourDetail === null || user === null) {
    //   window.location.href = "/";
    //   sessionStorage.setItem("messagePay", "Error user or tour inf");
    // } else {
    //   sessionStorage.setItem("messagePay", "");
    // }
    let begin_date = tourDetail.begin_date.split(" ");
    let end_date = tourDetail.finish_date.split(" ");
    let { data, errors } = this.state;

    return (
      <div>
        <div className="coverLoader">
          <div className="loader"></div>
        </div>
        <div className="payForm">
          <div className="inputInfoPay">
            <div
              className={this.state.isDisabledPay ? "paypal_pay hidden" : ""}
            >
              <h2>Select payment</h2>
              <hr />
              <p className="guider_aggument">
                Please read our terms carefully and click checkbox below to continue....<br/>
                1.  Maximum number of people join in trip is 8 people.<br/>
                2.  Fee for a child equals 50% fee of one adult.<br/>
                3.  You can review Guider after finish their trips.<br/>
                4.  If Your Guider accept then cancel your trip, TWL system will refund 100% fee to you.<br/>
                5.  You will not be refunded If you cancel a trip after 24 hours before begin time of that trip.<br/>
                6.  You will be refunded 100% fee If you cancel a trip before 24 hours before begin time of that trip.<br/>
            </p>

              <input type="checkbox" onClick={this.handleCheckBox} /> <span className="policy" >I agree with agguments</span>
              <div className="paypal_radio">
                {
                  this.state.isDisabledCheckBox ? ''
                    : <input
                      type="radio"
                      name="paypal"
                      onChange={this.goToPayPal}
                    />
                }{" "}
                <span>Paypal</span>
                <img src="/img/paypal.png" alt="paypal" />
              </div>
              <button className="payBack" onClick={() => { window.location.href = '/chatbox/' + tourDetail.guider_id + '/' + tourDetail.post_id }}>Go back</button>
            </div>
            <div
              className={this.state.isDisabledPay ? "" : "paypal_pay hidden"}
            >

              <div className="infoTravellerPay">
                <p>First name</p>
                <input
                  type="text"
                  placeholder="Fisrt name"
                  name="first_name"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['first_name'] ? <p style={{ color: "red" }} className="errorInput">{errors['first_name']}</p> : ''}

                <p>Last name</p>
                <input
                  type="text"
                  placeholder="Last name"
                  name="last_name"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['last_name'] ? <p style={{ color: "red" }} className="errorInput">{errors['last_name']}</p> : ''}

                <p>Phone</p>
                <select
                  style={{
                    width: "40%",
                    height: "42px",
                    border: "1px solid #eaeaea"
                  }}
                  name='country_phone'
                  value={data.country_phone}
                  onChange={e => {
                    this.handleChange(e);
                  }}
                >
                  {country_phone}
                </select>
                <input
                  type="text"
                  placeholder="Phone"
                  className="phone_traveller"
                  name="phone"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['phone'] ? <p style={{ color: "red" }} className="errorInput">{errors['phone']}</p> : ''}
                <input
                  type="submit"
                  value="Save"
                  className="saveInfoTraveller"
                  onClick={this.submitForm}
                />
              </div>
            </div>
          </div>

          {/* infoTourBook */}
          <div className="infoTourBook">
            <div className="intro_tour">
              <img className="payImg" alt="natural" src={`${postInfo.picture_link[0]}`} />
              <h2 className="titleTourPay">{postInfo.title}</h2>
              <div className="tool-tipPost">
                {/* Post */}
                <div id="reactContainer">
                  {/*  Content  */}
                  <div className="content " id="contentPay">
                    <div className="content-right " id="content-rightPay">
                      <div className="PostDetail">
                        <div className="intro">
                          <iframe
                            src={this.state.link_youtube}
                            frameBorder="0"
                            allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                          ></iframe>
                          <h2 className="titleTour">{postInfo.title}</h2>
                          <p className="introduceTour">{postInfo.description}</p>
                        </div>
                        <div className="activities">
                          <ul>
                            <li>
                              <i className="fa fa-map-marker"></i>
                              <span>{postInfo.location}</span>
                            </li>
                            <li>
                              <i className="fa fa-hourglass-end" />
                              <span>{postInfo.total_hour} hours</span>
                            </li>
                            <li>
                              <i className="fa fa-users" />
                              <span>Private tour. Only you and your host</span>
                            </li>
                          </ul>
                          <i className="fa fa-sticky-note" />
                          <span>
                            <strong>Including:</strong>
                            {postInfo.including_service}
                          </span>
                        </div>

                      </div>
                    </div>
                  </div>
                </div>
                {/* End Post */}
              </div>
            </div>
            <div className="info_tourDetail">
              <hr />
              <div className="tour_detailHour">
                <i className="fa fa-calendar-o celander" aria-hidden="true">
                  <span>Begin date: {begin_date[0]}</span>
                </i>
                <br />
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>Begin time: {begin_date[1]}</span>
                </i>
                <br />

                <i className="fa fa-calendar-o celander" aria-hidden="true">
                  <span>End date: {end_date[0]}</span>
                </i>
                <br />
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>End time: {end_date[1]}</span>
                </i>
                <br />
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>Tour duration: {postInfo.total_hour} hour</span>
                </i>
                <br />
                <i className="fa fa-user" aria-hidden="true" style={{ position: 'relative' }}>
                  <span >
                    Guider: <span className="guiderNamePay">{tourDetail.guider_name}</span>
                  </span>
                  <div className="tool_tipGuider">
                    <div className="content-left">
                      <GuiderInPost
                        guiderId={tourDetail.guider_id}
                        postId={tourDetail.post_id}
                      />
                    </div>
                  </div>
                </i>
                <br />
                <i className="fa fa-user" aria-hidden="true">
                  <span >
                    Amount people:
                    <span style={{ color: "#e71575", position: 'relative', cursor: 'pointer' }} id="amout_show">
                      {" "}{parseInt(tourDetail.adult_quantity) +
                        parseInt(tourDetail.children_quantity)}
                    </span>
                    {" "}people
                  </span>
                  <div className="tool_tipPeople">
                    <i className="fa fa-user" aria-hidden="true">
                      <span>
                        Adult:{" " + tourDetail.adult_quantity}
                      </span>
                    </i>
                    <p></p>
                    <i className="fa fa-user" aria-hidden="true">
                      <span>
                        Children:{" " + tourDetail.children_quantity}
                      </span>
                    </i>
                    <p></p>
                  </div>
                </i>
                <hr />
                <div className="total_priceBook">
                  Fee tour:
                  <span>{tourDetail.price}$</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
function mapStateToProps(state) {
  const user = state.user;
  return { user };
}
export default connect(mapStateToProps)(Pay);
