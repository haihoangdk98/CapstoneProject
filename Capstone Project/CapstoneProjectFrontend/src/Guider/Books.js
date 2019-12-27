import React, { Component } from "react";
import { Link } from "react-router-dom";
import $ from "jquery";
import Config from "../Config";
import { connect } from "react-redux";
import SweetAlert from 'react-bootstrap-sweetalert';
class GuiderBooks extends Component {
	constructor(props) {
		super(props);
		this.state = {
			alert: null,
			orders: [],
			status: "WAITING",
			page: 0,
			cors: {
				method: "GET",
				mode: "cors",
				credentials: "include",
				headers: {
					Accept: "application/json"
				}
			}
		};

		this.accept = this.accept.bind(this);
		this.deny = this.deny.bind(this);
		this.cancel = this.cancel.bind(this);
		//this.notification = this.notification.bind(this);
	}
	async accept(eve) {
		try {
			$(".coverLoader").css("height", "1800px");
			$('.coverLoader').show();
			const remain = Object.assign([],this.state.orders);
			remain.splice(eve.target.id, 1);
			let { cors } = this.state;
			const orderResponse = await fetch(
				Config.api_url + "Order/AcceptOrder/" + eve.target.value,
				cors
			);

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			} 
				$('.coverLoader').hide();
				this.setState({ orders: remain });
			


		} catch (err) {
			
			$('.coverLoader').hide();
			this.notification("Sorry, Your time on this trip have already occupied, please check you schedule");
			console.log(err);
		}
	}

	async deny(eve) {
		eve.preventDefault();
		$(".coverLoader").css("height", "1800px");
		$('.coverLoader').show();
		try {
			const denied = this.state.orders[eve.target.id];
			const remain = Object.assign([],this.state.orders);
			remain.splice(eve.target.id, 1);
			const orderResponse = await fetch(
				Config.api_url + "Order/refuseTrip/" + eve.target.value,
				{
					method: "PUT",
					mode: "cors",
					credentials: "include",
					body: JSON.stringify(denied)
				}
			);

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}

			const order = await orderResponse.text();
			//console.log(order);
			$('.coverLoader').hide();
			this.setState({ orders: remain });
		} catch (err) {
			$('.coverLoader').hide();
			this.notification("Sorry, Can not refund your client right now. Please try another time");
			console.log(err);
		}
	}

	async cancel(eve) {
		eve.preventDefault();
		$(".coverLoader").css("height", "1800px");
		$('.coverLoader').show();
		try {
			//console.log(this.state.orders);
			const denied = this.state.orders[eve.target.id];
			const remain = Object.assign([],this.state.orders);
			remain.splice(eve.target.id, 1);
			const orderResponse = await fetch(
				Config.api_url +
				"Order/CancelOrderAsGuider?trip_id=" +
				eve.target.value,
				{
					method: "PUT",
					mode: "cors",
					credentials: "include",
					body: JSON.stringify(denied)
				}
			);

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}

			const order = await orderResponse.text();
			//console.log(order);
			$('.coverLoader').hide();
			this.setState({ orders: remain });
		} catch (err) {
			$('.coverLoader').hide();
			this.notification("Sorry, Can not refund your client right now. Please try another time");
			console.log(err);
		}
	}

	onNotification() {
		this.setState({ alert: null });
	}

	notification(notification) {
		const getAlert = () => (
			<SweetAlert
				warning
				confirmBtnText="Close"
				confirmBtnBsStyle="danger"
				title="Notification"
				onConfirm={() => this.onNotification()}
			>
				{notification}
			</SweetAlert>
		);
		this.setState({
			alert: getAlert()
		});
	}

	async componentDidMount() {
		let user = this.props.user;
		$('.tvlTab li[value="WAITING"]').addClass('selected');
		let { cors } = this.state;
		try {
			const orderResponse = await fetch(
				Config.api_url +
				"Order/GetOrderByStatus?role=" +
				"GUIDER" +
				"&id=" +
				user.id +
				"&status=" +
				this.state.status + "&page=0",
				cors
			);


			const respone = await fetch(
				Config.api_url +
				"Order/GetOrderByStatusPageCount?role=" +
				"GUIDER" +
				"&id=" +
				user.id +
				"&status=" +
				this.state.status,
				cors
			);

			const order = await orderResponse.json();
			const totalPage = await respone.json();
			this.setState({ orders: order, totalPage });
		} catch (err) {
			console.log(err);
		}
		let $li = $('.tvlTab li').click(function () {
			$li.removeClass('selected');
			$(this).addClass('selected');
		});
	}

	async tabList(status, currentPage = 0) {
		let user = this.props.user;
		let { cors } = this.state;
		try {
			const orderResponse = await fetch(
				Config.api_url +
				"Order/GetOrderByStatus?role=" +
				"GUIDER" +
				"&id=" +
				user.id +
				"&status=" +
				status + "&page=" + currentPage,
				cors
			);

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}
			const respone = await fetch(
				Config.api_url +
				"Order/GetOrderByStatusPageCount?role=" +
				"GUIDER" +
				"&id=" +
				user.id +
				"&status=" +
				status,
				cors
			);

			if (!orderResponse.ok) {
				throw Error(orderResponse.status + ": " + orderResponse.statusText);
			}
			const order = await orderResponse.json();
			const totalPage = await respone.json();
			this.setState({ orders: order, status: status, totalPage });
		} catch (err) {
			console.log(err);
		}
	}


	range = (start, end) => {
		var ans = [];
		for (let i = start; i <= end; i++) {
			ans.push(i);
		}
		return ans;
	}

	handleCurrentPage = (currentPage) => {
		let { status } = this.state;
		this.tabList(status, currentPage);

		this.setState({
			page: currentPage
		});

	}

	render() {
		let { status, orders, totalPage, page } = this.state;
		//pagination
		const range = this.range(0, totalPage - 1);
		let renderPageNumbers = totalPage === 1 ? '' :
			range.map((i) => (
				<button
					key={i}
					id={i}
					onClick={() => this.handleCurrentPage(i)}
					className={page === i ? "currentPage" : ''}
				>
					{i + 1}
				</button>
			)
			);
		if(status === 'WAITING') {
			orders = orders.sort((pre,next) => {
				return pre.book_time - next.book_time;
			});
		}
		let order = orders.map((order, index) => (
			<tr className="row100 body" key={index}>
				<td className="cell100 column2">
					<Link to={`/reviewtvl/` + order.traveler_id}>{order.object}</Link>
				</td>
				<td className="cell100 column3">{order.begin_date}</td>
				<td className="cell100 column4">{order.finish_date}</td>
				<td className="cell100 column5">
					<Link to={`/post/${order.post_id}`}>{order.postTitle}</Link>
				</td>
				<td className="cell100 column6">{order.adult_quantity}</td>
				<td className="cell100 column7">{order.children_quantity}</td>
				<td className="cell100 column8">{order.fee_paid}$</td> 
				{status === 'WAITING' ? (
					<td className="cell100 column1">
						<div>
							<button
								className="accept btn btn-primary btn-icon-split"
								value={order.trip_id}
								id={index}
								onClick={this.accept}
								type="button"
							>
								Accept
            </button>
							<button
								onClick={this.deny}
								value={order.trip_id}
								id={index}
								className="refuse btn btn-danger btn-icon-split "
								type="button"
								style={{ marginLeft: '10px' }}
							>
								Deny
            </button>
						</div>
					</td>
				) : null}
				{status === 'ONGOING' ? (
					<td className="cell100 column1">
						<button
							className="cancel btn btn-secondary"
							value={order.trip_id}
							id={index}
							onClick={this.cancel}
							type="button"
						>
							Cancel
            </button>
					</td>
				) : null}
			</tr>
		));


		let arr = ["WAITING", "ONGOING", "FINISHED", "CANCELLED"];

		let tab = arr.map((value, index) => (
			<li key={index} onClick={() => { this.tabList(value); }} value={value} >
				{value}
			</li>
		));

		return (
			<div className="tvlManager_Container">
				{this.state.alert}
				<div className="coverLoader">
					<div className="loader"></div>
				</div>
				<div className="tvlManager_title">
					<ul className="tvlTab" id="bookManage">{tab}</ul>
				</div>

				<div className="table100 ver1">
					<div className="table100-firstcol">
						{/* <table>
              <thead>
                <tr className="row100 head">
                  <th className="cell100 column1">Action</th>
                </tr>
              </thead>
              <tbody>{action}</tbody>
            </table> */}
					</div>
					<div className="wrap-table100-nextcols js-pscroll ps ps--active-x">
						<div className="table100-nextcols">
							{
								orders.length === 0 ? <h1>You don't have any booking on that list</h1>
									:
									<table>
										<thead>
											<tr className="row100 head">
												<th className="cell100 column2">Traverler</th>
												<th className="cell100 column3">Start time</th>
												<th className="cell100 column4">End time</th>
												<th className="cell100 column5">Post</th>
												<th className="cell100 column6">Adult quantity</th>
												<th className="cell100 column7">Child quantiy</th>
												<th className="cell100 column8">Price</th>
												{status === 'WAITING' || status === 'ONGOING' ? <th className="cell100 column8">Action</th> : null}
											</tr>
										</thead>
										<tbody>{order}</tbody>
									</table>
							}
						</div>

						<div className="pagination">
							<div className="paginationContent">
								{renderPageNumbers}
							</div>
						</div>
					</div>
				</div>
				<div></div>
			</div>
		);
	}


}
function mapStateToProps(state) {
	const user = state.user;
	return { user };
}
export default connect(mapStateToProps)(GuiderBooks);
