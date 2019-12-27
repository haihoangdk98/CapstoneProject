import React, { Component } from "react";
import Chatbox from "./Traveler/Chatbox";
import ProfileGuiders from "./Guider/ProfileGuiders";
import Home from "./common/Home";
import ProfileTravaller from './Traveler/ProfileTraveller';
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import { BrowserRouter } from "react-router-dom";
import Navbar from './Nav/Navbar';
import Footer from './common/Footer';
import LoggedTvl from './Nav/LoggedTvl';
import LoggedGuider from './Nav/LoggedGuider';
import PostDetail from './Guider/PostDetail';
import Tour from './Tour';
import PostTourDetail from './common/PostTourDetail';
import Pay from './Traveler/Pay';
import TravellerManager from './Traveler/TravellerManager';
import GuiderContract from './Guider/GuiderContract';
import Chart from './Guider/Chart';
import ManagePost from './Guider/ManagePost';
import AddPost from './Guider/AddPost';
import { connect } from 'react-redux';
import { wsConnect, wsDisconnect, send } from './redux/webSocket';
import Message from './common/Message';
import ChatList from './common/ChatStore';
import Books from './Guider/Books';
import Schedule from './Guider/Schedule';
import GuiderProfile from './Guider/GuiderProfile';
import ChangePassword from "./common/ChangePassword";
import EditPost from './Guider/EditPost';
import SweetAlert from 'react-bootstrap-sweetalert';
import Page404 from './Page404';
import ReviewTraveler from './Guider/ReviewTraveler';
import Config from './Config';
import ForgotPassword from "./common/ForgotPassword";

class App extends Component {
	constructor(props) {
		super(props);
		console.log("constructor app");
		console.log(props);
		this.state = {
			userName: "Guest",
			role: "GUEST",
			id: 0,
			logedIn: false,
			avartar: "",
			
			alert: null,
		};

	}
	

	

	componentDidMount() {

		// if (typeof (Storage) !== 'undefined') {
		// 	// get sessionStorage
		// 	let user = window.sessionStorage.getItem('user');
		// 	if (user !== null) this.reload(JSON.parse(user));
		// } else {
		// 	alert('Browser not support!');
		// }
		let user = this.props.user;
		if (user.logedIn) {
			this.props.dispatch(wsConnect(Config.api_url+"ws"));
		} else {

		}

	}

	
	render() {
		// let present;
		// let user = JSON.parse(window.sessionStorage.getItem('user'));
		// if (this.state.logedIn) {
		// 	if (user.role === 'TRAVELER') {
		// 		present = <LoggedTvl reload={this.reload} />;
		// 	}
		// 	else {
		// 		present = <LoggedGuider reload={this.reload} />;
		// 	}
		// } else {
		// 	present = <Navbar reload={this.reload} user={this.props.user}/>;
		// }
		

		let present;
		let user = this.props.user;
		if (user.logedIn) {
			if (user.role === 'TRAVELER') {
				present = <LoggedTvl id={this.props.user.id} reload={this.reload} />;
			}
			else {
				present = <LoggedGuider id={this.props.user.id} reload={this.reload} />;
			}
		} else {
			present = <Navbar reload={this.reload} />;
		}
		return (
			<div className="App">
				{present}
				<Switch>

					<Route path='/' component={Home} exact />
					<Route path='/guider/:guider_id' component={ProfileGuiders} exact />
					<Route path='/post/:post_id' component={PostDetail} exact />
					<Route exact path='/chatbox/:guiderId/:post_id/:message' component={Chatbox} />
					<Route exact path='/chatbox/:guiderId/:post_id/' component={Chatbox} />
					<Route path='/profileguiders'><GuiderProfile /> </Route>
					<Route path='/tour' component={Tour} />
					<Route path='/profiletraveller' component={ProfileTravaller} />
					<Route path='/tour/:id' component={PostTourDetail} exact />
					<Route path='/posttour/:id' component={PostTourDetail} />
					<Route path='/book' component={Pay} />
					<Route path='/tvlManager'> <TravellerManager id={this.state.id} /> </Route>
					<Route path='/contract' component={GuiderContract} />
					<Route path='/chart' component={Chart} />
					<Route path='/forgotpassword' component={ForgotPassword} />
					<Route path='/add' ><AddPost guiderId={this.props.user.id} /></Route>
					<Route path='/managebook' ><Books id={this.props.user.id} /></Route>
					<Route path='/schedule' ><Schedule id={this.props.user.id} /></Route>
					<Route path='/changepassword' ><ChangePassword guiderId={this.props.user.id} /></Route>
					<Route path='/chat' ><Message /></Route>
					<Route exact path={"/edit"}><ManagePost guiderId={this.props.user.id} /></Route>
					<Route path={"/update/:guider/:post"} component={EditPost} />
					<Route path='/reviewtvl/:id' component={ReviewTraveler} />
					{/* <Route path='/profileguider' component={ProfileGuiders} /> */}
					<Route path='*' component={Page404} />
				</Switch>
				<Footer />
			</div>

		);
	}
}
function mapStateToProps(state) {
	console.log(state);
	const messages = state.messages;
	const clients = state.clients;
	const user = state.user;
	const error = state.Error;
	const notifications = state.notifications; 
	return { messages, clients, user, error, notifications };
}
App = connect(mapStateToProps)(App);

export default App;
//export default connect()(App);
