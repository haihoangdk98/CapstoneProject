import React, { Component } from 'react';
import "font-awesome/css/font-awesome.min.css";
import $ from 'jquery';
import Config from '../Config';
import ReactDOM from 'react-dom';
import SweetAlert from 'react-bootstrap-sweetalert';
import { connect } from 'react-redux';
import { signIn } from '../redux/actions';
import { wsConnect, wsDisconnect, send } from '../redux/webSocket';
import { Redirect } from "react-router-dom";
class Navbar extends Component {
    constructor(props) {
        super(props);
        this.state = {
            alert: null,
            data: {
                userName: "",
                password: "",
                re_password: '',
                role: "TRAVELER",
                email: ""
            },
            login: {
                userName: "",
                password: "",
                role: "TRAVELER"
            },
            errors: [],
            isError: false,
            activeSearch: false
        }
        this.wrapperRef = React.createRef();
        this.signUp = this.signUp.bind(this);
        this.logIn = this.logIn.bind(this);

    }

    genderOnChange = e => {
        let { data } = this.state;
        data[e.target.name] = e.target.value;
        this.setState({ data });
    };

    genderOnChangeLogin = e => {
        let { login } = this.state;
        login[e.target.name] = e.target.value;
        this.setState({ login });
    };

    validateEmail(email) {
        const pattern = /[a-zA-Z0-9]+[\.]?([a-zA-Z0-9]+)?[\@][a-z]{3,9}[\.][a-z]{2,5}/g;
        const result = pattern.test(email);
        return result;
    }

    handleChange = (e) => {
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

    handleChangeLogin = (e) => {
        const value = e.target.value;
        const name = e.target.name;
        const { login } = this.state;
        login[name] = value;
        this.setState({ login });
    }

    isValidate = () => {
        const { data } = this.state;
        let isError = false;
        let errors = {};
        if (data.userName === '') {
            isError = true;
            errors['userName'] = 'User name is empty, Input your user name';
        }
        if (data.password.length < 8) {
            isError = true;
            errors['password'] = 'Password consists of 8 characters or more';
        }
        if (this.validateEmail(data.email) === false) {
            isError = true;
            errors['email'] = 'Email example like googleemail@gmail.com';
        }
        if (data.re_password !== data.password) {
            isError = true;
            errors['re_password'] = 'Re-password is not the same as a password';
        }

        this.setState({ isError, errors });
        if (isError)
            return true;

        return false;
    }

    resetText = () => {
        let data = {
            userName: "",
            password: "",
            re_password: '',
            role: "TRAVELER"
        }
        let login = {
            userName: "",
            password: "",
            role: "TRAVELER"
        }
        let errors = []
        let isError = false

        this.setState({ data, errors, isError, login })
    }

    async signUp(eve) {
        eve.preventDefault();
        if (this.isValidate()) {
            return false;
        }

        let { data, errors } = this.state;

        try {
            const response = await fetch(Config.api_url + "account/registrator",
                {
                    method: "POST",
                    mode: "cors",
                    credentials: "include",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(data)
                }
            );
            if (response.status !== 200) {
                errors['userName'] = 'This account name already existed';
                this.setState({ errors });
                return false;
            }
            const user = await response.json();
            $('.signUpForm').hide();
            this.statusProfile('Welcome come to our website');
            // this.props.reload.call(this, await user);
        } catch (err) {
            console.log(err);
        }


    }



    async logIn(eve) {
        eve.preventDefault();
        let { login, errors } = this.state;
        try {
        //     const response = await fetch(Config.api_url + "account/login",
        //         {
        //             method: "POST",
        //             mode: "cors",
        //             credentials: "include",
        //             headers: {
        //                 'Content-Type': 'application/json',

        //             },
        //             body: JSON.stringify(login)
        //         }
        //     );
        //     if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
        //     const user = await response.json();
        //     // console.log(await user);
        //     if(user.role !== login.role){
        //         errors['role'] = 'Role is wrong';
        //         this.setState({errors}); 
        //         return false;
        //     }
        //     this.props.reload.call(this, await user);
            this.props.dispatch(signIn(login));
            this.props.dispatch(wsConnect(Config.api_url+"ws"));
            //window.location.href = "/";
            
            // this.props.reload.call(this,  this.props.user);
        } catch (err) {
            console.log(err);
            errors['login'] = 'User name or password is wrong';
            this.setState({ errors });
        }
    }

    hideAlert() {
        this.setState({
            alert: null
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

    

    componentDidMount() {
        $("head").append('<link href="/css/login.css" rel="stylesheet"/>');
        $("head").append('<link href="/css/navbar.css" rel="stylesheet"/>');

        $('.button-group > button').on('click', function () {
            $('.button-group > button').removeClass('active');
            $(this).addClass('active');
        });


        $('input[name=search]').focus(function () {
            $('#searchNav #fillterNav').show();

        });

        $(document).mouseup(function (e) {
            if (!$('#searchNav').is(e.target) && !$('#fillterNav').is(e.target)
                && $('#searchNav').has(e.target).length === 0
                && $('#fillterNav').has(e.target).length === 0) {
                $('#fillterNav').hide();
            }
        });

        // click sign up and close sign up form
        $('.signup').click(function () {
            $('.signUpForm').show();
        });

        $('.closeLogin').click(function () {
            $('.signUpForm,.loginForm').hide();
        });
        // click login and close login form

        $('.login').click(function () {
            $('.loginForm').show();
        });

        $('.SpanLogin').click(function () {
            $('.loginForm').show();
            $('.signUpForm').hide();
        });


        $('.SignIn').click(function () {
            $('.signUpForm').show();
            $('.loginForm').hide();
        });

        //mouse click outside .content-login form
        $(document).mouseup(function (e) {
            if (e.button === 0) {
                var container = $(".content-login");
                // if the target of the click isn't the container nor a descendant of the container
                if (!container.is(e.target) && container.has(e.target).length === 0) {
                    $('.signUpForm,.loginForm').hide();
                }
            }
        });

        // hide and show password in login
        $(document).on('click', '.fa-eye', function () {
            var input = $("#pass_log_id");
            input.attr('type') === 'password' ? input.attr('type', 'text') : input.attr('type', 'password');
        });

        //reset text when click out a div with reactjs
        document.addEventListener('click', this.handleClick)



    }

    handleClick = (event) => {
        const { target } = event
        if (!this.wrapperRef.current.contains(target)) {
            let data = {
                userName: "",
                password: "",
                re_password: "",
                role: "TRAVELER",
                email: ""
            }
            let errors = []
            let isError = false
            this.setState({ data, errors, isError });
        }
    }

    componentWillUnmount() {
        // important
        document.removeEventListener('click', this.handleClick);
        
    }


    render() {
        let { data, errors, login } = this.state;
        if (this.props.error.flag) {
			errors['login'] = 'User name or password is wrong';
		}
        
        let path = window.location.pathname;
        return (
            <div>
                {this.state.alert}
                {/* sign up */}
                <div className="layout signUpForm">
                    <div className="content-login" ref={this.wrapperRef}>
                        <button className="closeLogin">
                            <i className="fa fa-times" onClick={this.resetText} />
                        </button>
                        <h3 className="SubTitle-230L-">Sign up </h3>
                        <form style={{ textAlign: "center" }} onSubmit={this.signUp}>
                            <div className="SignupForm-20HPb">
                                <div className="gender" style={{ marginBottom: '15px', marginTop: '15px' }}>
                                    <input
                                        type="radio"
                                        className="gendermale"
                                        name="role"
                                        value="TRAVELER"
                                        checked={data.role === 'TRAVELER'}
                                        onChange={e => this.genderOnChange(e)}
                                    />{" "}
                                    Traveler
                                    <input
                                        type="radio"
                                        name="role"
                                        value="GUIDER"
                                        checked={data.role === 'GUIDER'}
                                        onChange={e => this.genderOnChange(e)}
                                    />{" "}
                                    Guider
                                </div>
                                <div className="UserName">
                                    <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                        Nick name *
                                </label>
                                    <input
                                        className="Input-1e6rU"
                                        type="text"
                                        name="userName"
                                        placeholder="User name"
                                        value={data.userName}
                                        onChange={(e) => { this.handleChange(e) }}
                                    />
                                    {errors['userName'] ? <p style={{ color: "red" }} className="errorInput">{errors['userName']}</p> : ''}
                                </div>
                                <div className="PasswordInput-1Qf5F">
                                    <div className="password">
                                        <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                            Password
                                </label>
                                        <input
                                            className="Input-1e6rU"
                                            placeholder="Password"
                                            type="password"
                                            name="password"
                                            value={data.password}
                                            onChange={(e) => { this.handleChange(e) }}
                                        />
                                        {errors['password'] ? <p style={{ color: "red" }} className="errorInput">{errors['password']}</p> : ''}
                                    </div>
                                </div>
                                <div className="lastName">
                                    <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                        Confirm password
                                </label>
                                    <input
                                        className="Input-1e6rU"
                                        type="password"
                                        name="re_password"
                                        placeholder="Re-password"
                                        value={data.re_password}
                                        onChange={(e) => { this.handleChange(e) }}
                                    />
                                    {errors['re_password'] ? <p style={{ color: "red" }} className="errorInput">{errors['re_password']}</p> : ''}
                                </div>
                                <div className="lastName">
                                    <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                        Confirm password
                                </label>
                                    <input
                                        className="Input-1e6rU"
                                        type="text"
                                        name="email"
                                        placeholder="Email"
                                        value={data.email}
                                        onChange={(e) => { this.handleChange(e) }}
                                    />
                                    {errors['email'] ? <p style={{ color: "red" }} className="errorInput">{errors['email']}</p> : ''}
                                </div>
                            </div>

                            <div className="Submit-2es0L">
                                <button type="submit" className="Button-2iSbC SubmitButton-3lXjw">
                                    <span className="SubmitText-sXv20">Join Withlocals</span>
                                </button>
                            </div>

                            <div className="loginLinkContain">
                                <button className="loginLink">
                                    <span className="SpanReady">I already have an account.</span>
                                    <span className="SpanLogin"> Log in</span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                {/* end sign up */}
                {/* login */}
                <div className="layout loginForm">
                    <div className="content-login">
                        <button className="closeLogin">
                            <i className="fa fa-times" onClick={this.resetText} />
                        </button>
                        <h3 className="SubTitle-230L-">Log in</h3>
                        <form style={{ textAlign: "center" }} onSubmit={this.logIn}>
                            <div className="SignupForm-20HPb">
                                {errors['role'] ? <p style={{ color: "red" }} className="errorInput">{errors['role']}</p> : ''}
                                <div className="gender" style={{ marginBottom: '15px', marginTop: '15px' }}>
                                    <input
                                        type="radio"
                                        className="gendermale"
                                        name="role"
                                        value="TRAVELER"
                                        checked={login.role === 'TRAVELER'}
                                        onChange={e => this.genderOnChangeLogin(e)}
                                    />{" "}
                                    Traveler
                                    <input
                                        type="radio"
                                        name="role"
                                        value="GUIDER"
                                        checked={login.role === 'GUIDER'}
                                        onChange={e => this.genderOnChangeLogin(e)}
                                    />{" "}
                                    Guider
                                </div>
                                {this.props.error.flag ? <p style={{ color: "red" }} className="errorInput">{errors['login']}</p> : ''}
                                <div className="firstName">
                                    <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                        Nick name *
                                    </label>

                                    <input
                                        className="Input-1e6rU"
                                        type="text"
                                        name="userName"
                                        placeholder="Name"
                                        value={login.userName}
                                        onChange={(e) => { this.handleChangeLogin(e) }}
                                    />
                                </div>
                                <div className="PasswordInput-1Qf5F">
                                    <div className="password">
                                        <label className="InputLabel-Tch5j InputLabelConditionalHide-24VTo">
                                            Password
                            </label>
                                        <div className="showPass">
                                            <input
                                                id="pass_log_id"
                                                className="Input-1e6rU"
                                                placeholder="Password"
                                                type="password"
                                                name="password"
                                                value={login.password}
                                                onChange={(e) => { this.handleChangeLogin(e) }}

                                            />
                                            <i className="fa fa-eye" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="forgotPass">
                                <a href="/forgotpassword">I forgot my password</a>
                            </div>
                            <div className="Submit-2es0L">
                                <button type="submit" className="Button-2iSbC SubmitButton-3lXjw">
                                    <span className="SubmitText-sXv20">Login</span>
                                </button>
                            </div>
                            <div className="loginLinkContain">
                                <button className="loginLink">
                                    <span className="SpanReady">No account?</span>
                                    <span className="SignIn"> Sign up</span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                {/* end login */}
                {/* Menubar */}
                <nav className="navbar" id="navbar">
                    <div className="menubar">
                        <div className="containerMain">
                            <div className="navLeft">
                                <div className="logoContainer">
                                    <a href="/">
                                        <img src="/icon/iconMain.jpg" />
                                    </a>
                                </div>
                                
                                <div className="search" id="searchNav">
                                    <label>
                                        <input
                                            type="text"
                                            placeholder="Where do you want to go?"
                                            name="search"
                                            autoComplete="off"
                                        />
                                    </label>
                                    <div className="fillter" id="fillterNav">
                                        <div className="filter-Content">
                                            <div className="localsOrExperience">
                                                <h3 className="explore">Explore TravelWlocals</h3>
                                                <div className="button-group">
                                                    <button className="active">Guider</button>
                                                    <button>Location</button>
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
                                    <ul>
                                        
                                        <li>
                                            <a style={{cursor:"pointer"}} className="login">
                                                Log in
                                            </a>
                                        </li>
                                        <li>
                                            <a style={{cursor:"pointer"}} className="signup">
                                                Sign up
                                            </a>
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
    const error = state.Error;
    return { error};
}
export default connect(mapStateToProps)(Navbar);