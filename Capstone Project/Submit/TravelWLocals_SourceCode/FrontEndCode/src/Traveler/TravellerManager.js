import React, { Component } from "react";
import $ from 'jquery';
import Config from '../Config';
import { Link } from 'react-router-dom';
import SweetAlert from 'react-bootstrap-sweetalert';
import {connect} from 'react-redux';
import { stat } from "fs";
class TravellerManager extends Component {

	constructor(props) {
		super(props);
		this.state = {
			orders: [],
			isDisable: false,
			isError: false,
			errorRate: false,
			hideAddComment: false,
			comment: '',
			info: {},
			rated: 0.0,
			alert: null,
			cmtExist: [],
			status: 'WAITING',
			first:0
		};
	}
	//check xem ng đăng nhập là ai nếu là guider thì về trang home
	componentWillMount() {
		//var user = JSON.parse(sessionStorage.getItem('user'));
		let user = this.props.user;
		if (user === null) {
			sessionStorage.setItem('messagePay', 'Error user or tour inf');
			window.location.href = '/';
		} else if (user.role === 'GUIDER') {
			sessionStorage.setItem('messagePay', 'You are Guider');
			window.location.href = '/';
		}
	}

	async componentDidMount() {//var user = JSON.parse(sessionStorage.getItem('user'));
		let user = this.props.user;
		try {
			const orderResponse = await fetch(
				Config.api_url + "Order/GetOrderByStatus?role=" + "TRAVELER" + "&id=" + user.id + "&status=WAITING",

				{
					method: "GET",
					mode: "cors",
					credentials: "include",
					headers: {
						'Accept': 'application/json'
					}
				});

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}

			const order = await orderResponse.json();
			this.setState({ orders: order });
		} catch (err) {
			console.log(err);
		}
		var $li = $('.tvlTab li').click(function () {
			$li.removeClass('selected');
			$(this).addClass('selected');
		});
	}

	async sendReview() {
		if (this.state.comment === '') {
			this.setState({ isError: true });
		} else {
			if (this.state.rated === 0) {
				this.setState({ errorRate: true })
			} else {
				var { info, rated } = this.state;
				var user = JSON.parse(sessionStorage.getItem('user'));
				var data = {}
				data.trip_id = info.order_id;
				data.review = this.state.comment;
				data.rated = rated;
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
				let response = await fetch(Config.api_url + 'review/create', options);
				response = await response.text();
				this.setState({ isDisable: false, isError: false, comment: '' });
				this.statusProfile('Comment Success');
			}
		}
	}

	async showReview(order_id, guider_id, post_id) {
		let commentRespone = await fetch(
			"http://localhost:8080/review/checkExist?trip_id=" + order_id,
			{
				method: "GET",
				mode: "cors",
				credentials: "include",
				headers: {
					'Accept': 'application/json'
				}
			});
		if (commentRespone.status === 404) {

			var { info } = this.state;
			info.order_id = order_id;
			info.guider_id = guider_id;
			info.post_id = post_id;
			this.setState({ isDisable: true, info });
		} else {
			let comment = await commentRespone.json();
			this.setState({ comment: comment[0].review, rated: comment[0].rated, isDisable: true, hideAddComment: true });
		}
	}


	closeReview() {
		this.setState({ isDisable: false, isError: false, errorRate: false, comment: '', hideAddComment: false });
	}


	getDataComment = (e) => {
		var value = e.target.value;
		this.setState({ comment: value, isError: false });
	}

	async tabList(status) {
		let user = this.props.user;
		try {
			const orderResponse = await fetch(
				Config.api_url + "Order/GetOrderByStatus?role=" + "TRAVELER" + "&id=" + user.id + "&status=" + status,
				{
					method: "GET",
					mode: "cors",
					credentials: "include",
					headers: {
						'Accept': 'application/json'
					}
				});

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}
			const order = await orderResponse.json();
			this.setState({ orders: order, status: status });
		} catch (err) {
			console.log(err);
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
				title="Thank you"
				onConfirm={() => this.hideAlert()}
			>
				{message}
			</SweetAlert>
		);

		this.setState({
			alert: getAlert()
		});
	}

	handleCancle = async (trip,status) => {
		$(".coverLoader").show();
		try {
			const orderResponse = await fetch(
				Config.api_url + "Order/CancelOrderAsTraveler?trip_id=" + trip,
				{
					method: "GET",
					mode: "cors",
					credentials: "include",
					headers: {
						'Accept': 'application/json'
					}
				});

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}
			$(".coverLoader").hide();
			this.tabList(status)
		} catch (err) {
			console.log(err);
		}
	}

	render() {
		let {first} = this.state;
		let order = this.state.orders.map((order, index) => {
			let { status } = this.state;
			return (
				<tr className="row100 body" key={index}>
					<td className="cell100 "><Link to={'/guider/' + order.guider_id} style={{ color: '#e71575' }}>{order.object}</Link></td>
					<td className="cell100 ">{order.begin_date}</td>
					<td className="cell100 ">{order.finish_date}</td>
					<td className="cell100 "><Link to={'/post/' + order.post_id} style={{ color: '#e71575' }}>{order.postTitle}</Link></td>
					<td className="cell100 ">{order.adult_quantity}</td>
					<td className="cell100 ">{order.children_quantity}</td>
					<td className="cell100 ">{order.fee_paid}$</td>
					{status === "FINISHED" ? <td className="cell100 "><button type="button" className="btn btn-secondary" onClick={() => this.showReview(order.trip_id, order.guider_id, order.post_id)}>Review</button></td> : ''}
					{status === "ONGOING" ? <td className="cell100 "><button type="button" className="btn btn-secondary" onClick={() => this.handleCancle(order.trip_id,status)}>Cancel</button></td> : ''}
					{status === "WAITING" ? <td className="cell100 "><button type="button" className="btn btn-secondary" onClick={() => this.handleCancle(order.trip_id,status)}>Cancel</button></td> : ''}
				
				</tr>
			)
		});
		var arr = ['WAITING', 'ONGOING', 'FINISHED', 'CANCELLED'];

		var tab = arr.map((value, index) =>
			<li key={index} onClick={() => { this.tabList(value) }}>{value}</li>
		);
		var { isDisable, isError, errorRate, hideAddComment, rated, status } = this.state;

		return (
			<div>
				{this.state.alert}
				<div className="coverLoader">
					<div className="loader"></div>
				</div>
				{isDisable ? <div className="layout_comment">
					<div id="comment_form">
						<div>
							<textarea rows="5" name="comment" id="comment" placeholder="Comment" value={this.state.comment} onChange={this.getDataComment}></textarea>
						</div>
						<div>
							<div className="stars">
								{rated === 5 ?
									<input className="star star-5" id="star-5" type="radio" name="star" defaultChecked onClick={() => { this.setState({ rated: 5.0 }) }} />
									:
									<input className="star star-5" id="star-5" type="radio" name="star" onClick={() => { this.setState({ rated: 5.0 }) }} />
								}
								<label className="star star-5" htmlFor="star-5"></label>

								{rated === 4 ?
									<input className="star star-4" id="star-4" type="radio" name="star" defaultChecked onClick={() => { this.setState({ rated: 4.0 }) }} />
									:
									<input className="star star-4" id="star-4" type="radio" name="star" onClick={() => { this.setState({ rated: 4.0 }) }} />
								}
								<label className="star star-4" htmlFor="star-4"></label>

								{rated === 3 ?
									<input className="star star-3" id="star-3" type="radio" name="star" defaultChecked onClick={() => { this.setState({ rated: 3.0 }) }} />
									:
									<input className="star star-3" id="star-3" type="radio" name="star" onClick={() => { this.setState({ rated: 3.0 }) }} />
								}
								<label className="star star-3" htmlFor="star-3"></label>
								{rated === 2 ?
									<input className="star star-2" id="star-2" type="radio" name="star" defaultChecked onClick={() => { this.setState({ rated: 2.0 }) }} />
									:
									<input className="star star-2" id="star-2" type="radio" name="star" onClick={() => { this.setState({ rated: 2.0 }) }} />
								}
								<label className="star star-2" htmlFor="star-2"></label>
								{rated === 1 ?
									<input className="star star-1" id="star-1" type="radio" name="star" defaultChecked onClick={() => { this.setState({ rated: 1.0 }) }} />
									:
									<input className="star star-1" id="star-1" type="radio" name="star" onClick={() => { this.setState({ rated: 1.0 }) }} />
								}

								<label className="star star-1" htmlFor="star-1"></label>
							</div>
							{isError ? <p style={{ color: 'red' }}>Comment before submit</p> : ''}
							{errorRate ? <p style={{ color: 'red' }}>Vote star before submit</p> : ''}
							<p></p>
							{hideAddComment ? '' : <input type="submit" name="submit" value="Add Comment" onClick={() => this.sendReview()} />}
							<input type="submit" name="submit" value="Close" onClick={() => this.closeReview()} />
						</div>
					</div>
				</div>
					: ''
				}
				<div className="tvlManager_Container">
					<div className="tvlManager_title">
						<ul className="tvlTab">
							{tab}
						</ul>
					</div>
					<div className="table100 ver1">
						<div className="wrap-table100-nextcols">
							<div className="table100-nextcols">
								<table>
									<thead>
										<tr className="row100 head">
											<th className="cell100 column2">Guider</th>
											<th className="cell100 column3">Start time</th>
											<th className="cell100 column4">End time</th>
											<th className="cell100 column5">Post</th>
											<th className="cell100 column6">Adult quantity</th>
											<th className="cell100 column7">Child quantiy</th>
											<th className="cell100 column8">Price</th>
											{status === "FINISHED" ? <th className="cell100 column8">Review</th> : ''}
											{status === "ONGOING" ? <th className="cell100 column8">Cancel</th> : ''}
											{status === "WAITING" ? <th className="cell100 column8">Cancel</th> : ''}
											{/* {first === 0 ? <th className="cell100 column8">Cancel</th> : ''} */}
										</tr>
									</thead>
									<tbody>
										{order}
									</tbody>
								</table>
							</div>

							<div className="wrap-table100-nextcols js-pscroll"></div>
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
export default connect(mapStateToProps)(TravellerManager);