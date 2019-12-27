
import { Redirect } from "react-router-dom";
import React, { Component } from "react";
import ReactDOM from 'react-dom';
import "font-awesome/css/font-awesome.min.css";
import $ from "jquery";
import country from '../json/country.json';
import Config from '../Config';
import SweetAlert from 'react-bootstrap-sweetalert';
import { connect } from 'react-redux';
class GuiderProfile extends Component {
      constructor(props) {
            super(props);
            this.state = {
                  countryList: country,
                  isError: false,
                  errors: {},
                  alert: null,
                  avatar_link: '',
                  avtImage: '',
                  language: [],
                  data: {

                        first_name: '',
                        last_name: '',
                        phone: '',
                        gender: '0',
                        email: '',
                        date_of_birth: '1970-01-01',
                        passion: '',
                        about_me: '',
                        language: ["Vietnamese"],
                        country: 'Vietnam',
                        city: '',
                        avatar: '',
                        // day: '01',
                        // month: '01',
                        // year: '1970',

                  }
            }
            //this.submitForm = this.submitForm.bind(this);
      }

      // componentWillMount() {
      //       var user = JSON.parse(sessionStorage.getItem('user'));
      //       if (user === null) {
      //             sessionStorage.setItem('messagePay', 'Error user or tour inf');
      //             window.location.href = '/';
      //       } else if (user.role === 'TRAVELER') {
      //             sessionStorage.setItem('messagePay', 'You are TRAVELER');
      //             window.location.href = '/';
      //       }
      // }

      async componentDidMount() {
            $("head").append('<link href="/css/profile_traveller.css" rel="stylesheet"/>');

            $("#avatar_trigger").click(function () {
                  $("#avatar_link").trigger('click');
            });
            //var user = JSON.parse(sessionStorage.getItem('user'));
            let user = this.props.user;
            const responseTraveller = await fetch(
                  Config.api_url + "Guider/getGuider/" + user.id,
                  {
                        method: "GET",
                        mode: "cors",
                        credentials: "include"
                  });


            const dataTraveller = await responseTraveller.json();
            var str = dataTraveller.date_of_birth;
            var res = str.split("/");
            dataTraveller.year = res[0];
            dataTraveller.month = res[1];
            dataTraveller.day = res[2].split(" ")[0];
            let link = dataTraveller.avatar;
            dataTraveller.avatar = this.fromDataURL(dataTraveller.avatar);
            console.log(dataTraveller);
            this.setState({ data: dataTraveller, avatar_link: link });


      }

      fromDataURL = url => {
            let downloadedImg = new Image();
            downloadedImg.crossOrigin = "Anonymous";
            downloadedImg.src = url;
            console.log(downloadedImg);
      }

      validatePhone(phone) {
            const pattern = /^\d{10,11}$/;
            const result = pattern.test(phone);
            return result;
      }

      handleChange = (e) => {
            const value = e.target.value;
            const name = e.target.name;
            let { errors } = this.state;
            if (value !== '') {
                  errors[name] = '';
            }

            const { data } = this.state;
            data[name] = value;
            if (name === "language") {
                  data[name] = [value];
            }
            this.setState({ data });
      }

      onImageChange = async (event) => {

            if (event.target.files && event.target.files[0]) {
                  let reader = new FileReader();
                  reader.readAsDataURL(event.target.files[0]);
                  reader.onload = (event) => {
                        this.state.data.avatar = reader.result;
                        this.setState({ avatar_link: event.target.result,
                         });
                  };
                  
            }
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
            if (data.slogan === '') {
                  isError = true;
                  errors['slogan'] = 'A good slogan will make you cool';
            }
            if (data.about_me === '') {
                  isError = true;
                  errors['about_me'] = 'Introduce yourself a bit, bro';
            }


            this.setState({ isError, errors });
            if (isError)
                  return true;

            return false;
      }

      hideAlert() {
            this.setState({
                  alert: null
            });
            //window.location.href = '/profileguiders';

      }

      statusProfile(message) {
            const getAlert = () => (
                  <SweetAlert
                        success
                        title="Woot!"
                        onConfirm={() => this.hideAlert()}
                  >
                        {message}
                  </SweetAlert>
            );

            this.setState({
                  alert: getAlert()
            });
      }

      createComplete = () => {
            var data = {

                  first_name: '',
                  last_name: '',
                  phone: '',
                  gender: '0',
                  date_of_birth: '1970-01-01',
                  slogan: '',
                  about_me: '',
                  language: ["Vietnamese"],
                  country: 'Vietnam',
                  city: '',
                  avatar: '',
                  // day: '01',
                  // month: '01',
                  // year: '1970',
            }
            this.setState({ data });
            this.statusProfile();

      }

      toBase64 = file => new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
      });

      async submitForm(e) {
            e.preventDefault();
            let user = this.props.user;

            // var user = JSON.parse(sessionStorage.getItem('user'));
            if (this.isValidate()) {
                  return false;
            }
            var { data, avtImage } = this.state;
            data.date_of_birth = data.year + "/" + data.month + "/" + data.day + " 00:00";
            delete data.year;
            delete data.month;
            delete data.day;
            data.languages = this.state.language;
            console.log(data);

            let options = {
                  method: 'POST',
                  mode: "cors",
                  credentials: "include",
                  headers: {
                        Accept: 'application/json',
                        'Content-Type': 'application/json',
                  },
                  body: JSON.stringify(data)
            };
            let response = await fetch(Config.api_url + 'Guider/Edit', options);
            response = await response.text();
            this.statusProfile('Update success!!');

      }

      render() {

            const { errors, data, avatar_link } = this.state;
            var day = [];
            for (var i = 1; i <= 31; i++) {
                  day.push(i);
            }
            var dayOption = day.map((value, index) => {
                  if (parseInt(value) < 10) {
                        return <option value={"0" + value} key={index}>{value}</option>
                  }
                  return <option value={value} key={index}>{value}</option>
            }
            )

            var month = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
            var monthOptiom = month.map((value, index) => {
                  if (parseInt(index + 1) < 10) {
                        return (<option value={"0" + (index + 1)} key={index}>{value}</option>)
                  }

                  return <option value={index + 1} key={index}>{value}</option>
            });

            var currentYear = parseInt(new Date().getFullYear());
            var year = [];
            for (var i = currentYear; i >= 1950; i--) {
                  year.push(i);
            }
            var yearOption = year.map((value, index) => (
                  <option value={value} key={index}>{value}</option>
            )
            )

            var country_name = this.state.countryList.map((value, index) => {
                  return <option value={value.name} data-code={value.dial_code} key={index}>{value.name}</option>
            })

            var language = ["English", "Vietnamese", "Japanese", "Chinese", "Korean", "French", "Russian", "Spanish"];
            var languageOption = language.map((value, index) => (
                  <p key={index}><input value={value} key={index} type="checkbox" onClick={(eve) => {
                        //eve.preventDefault();
                        if (eve.target.checked == true) this.state.language.push(eve.target.value);
                        else if (this.state.language.indexOf(eve.target.value) >= 0) {
                              this.state.language.splice(this.state.language.indexOf(eve.target.value), 1);
                        }
                  }} />{value}</p>
            ))
            return (
                  <div>
                        {this.state.alert}
                        <div className="containerMain">
                              <h1 className="h1-profile">Your profile</h1>
                              <div className="content-profile">
                                    <div className="profile-image">
                                          <h1 className="h1-introduce">Doan Anh</h1>
                                          <img
                                                alt="profile avatar"
                                                height={150}
                                                width={150}
                                                className="profile-avatar"
                                                src={avatar_link}
                                          />
                                          <br />
                                          <button className="btn-changepic" id="avatar_trigger">Change profile picture</button>
                                          <input type="file" id="avatar_link" style={{ display: 'none' }} name="avatar_link" onChange={this.onImageChange} />
                                    </div>

                                    <div className="information_Main">
                                          <div className="profile-information">
                                                <h2 className="h2-introduce">Introduce yourself</h2>
                                                <div className="boxInt">
                                                      <label className="label-information">First Name</label>
                                                      <input className="input-information" name="first_name" value={data.first_name} onChange={(e) => { this.handleChange(e) }} />
                                                      {errors['first_name'] ? <p style={{ color: "red" }} className="errorInput">{errors['first_name']}</p> : ''}
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Last Name</label>
                                                      <input className="input-information" name="last_name" value={data.last_name} onChange={(e) => { this.handleChange(e) }} />
                                                      {errors['last_name'] ? <p style={{ color: "red" }} className="errorInput">{errors['last_name']}</p> : ''}
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Gender</label>
                                                      <div>
                                                            <select className="form-control" name="gender" value={data.gender} onChange={(e) => { this.handleChange(e) }}>
                                                                  <option value="0">Male</option>
                                                                  <option value="1">Female</option>
                                                                  <option value="2">Other</option>
                                                            </select>
                                                      </div>
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Date Of Birth</label>
                                                      <div style={{ marginBottom: 30 }}>
                                                            <select className="DOB" name="day" value={data.day} onChange={(e) => { this.handleChange(e) }}>
                                                                  {dayOption}
                                                            </select>
                                                            <select className="DOB" name="month" value={data.month} onChange={(e) => { this.handleChange(e) }}>
                                                                  {monthOptiom}
                                                            </select>
                                                            <select className="DOB" name="year" value={data.year} onChange={(e) => { this.handleChange(e) }}>
                                                                  {yearOption}
                                                            </select>
                                                      </div>
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Phone</label>
                                                      <input className="input-information" name="phone" value={data.phone} onChange={(e) => { this.handleChange(e) }} />
                                                      {errors['phone'] ? <p style={{ color: "red" }} className="errorInput">{errors['phone']}</p> : ''}

                                                      {/* <div className="label-information">Email</div>
                                                            <input className="input-information" name="email" value={data.email} onChange={(e)=>{this.handleChange(e)}}/>
                                                            {errors['email'] ? <p style={{color: "red"}} className="errorInput">{errors['email']}</p> : ''} */}
                                                </div>
                                          </div>

                                          <div className="profile-information">
                                                <h2 className="h2-introduce">Where do you live?</h2>
                                                <div className="boxInt">
                                                      <label className="label-information">City or town</label>
                                                      <input
                                                            className="input-information"
                                                            style={{ marginBottom: 20 }}
                                                            name="city"
                                                            onChange={(e) => { this.handleChange(e) }}
                                                            value={data.city}
                                                      />
                                                </div>
                                          </div>

                                          <div className="last-information">
                                                <h2 className="h2-introduce">Some thing about you</h2>
                                                <div className="label-information">Your language</div>
                                                <div style={{ marginLeft: 30 }}>
                                                      {languageOption}
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Description</label>
                                                      <input
                                                            placeholder="Write something about you pls !!!"
                                                            className="input-something"
                                                            name="about_me"
                                                            onChange={(e) => { this.handleChange(e) }}
                                                            value={data.about_me}
                                                      />
                                                      {errors['about_me'] ? <p style={{ color: "red" }} className="errorInput">{errors['about_me']}</p> : ''}
                                                </div>
                                                <div className="boxInt">
                                                      <label className="label-information">Your passion, habit, favorite</label>
                                                      <input
                                                            placeholder="Share your habit to your clients !!!"
                                                            className="input-something"
                                                            name="passion"
                                                            onChange={(e) => { this.handleChange(e) }}
                                                            defaultValue={data.slogan}
                                                      />
                                                      {errors['slogan'] ? <p style={{ color: "red" }} className="errorInput">{errors['slogan']}</p> : ''}
                                                </div>
                                          </div>
                                          <div style={{ textAlign: "center" }}>
                                                <button className="btn-save" onClick={(e) => { this.submitForm(e) }}>Save Your Profile</button>
                                          </div>
                                    </div>
                              </div>
                        </div>
                        ;
      </div>
            );
      }
}
function mapStateToProps(state) {
      const user = state.user;
      return { user };
}
export default connect(mapStateToProps)(GuiderProfile);
