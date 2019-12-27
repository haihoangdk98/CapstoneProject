import React from "react";
import ReviewInPost from "../Guider/ReviewInPost";
import PlanInPost from "./PlanInPost";
import GuiderInPost from "./GuiderInPost";
import Rated from "./Rated";
import $ from "jquery";
import Config from "../Config";
import { ar } from "date-fns/locale";
import SweetAlert from 'react-bootstrap-sweetalert';
import { connect } from 'react-redux';
import {Link} from 'react-router-dom';
class PostDetail extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			statusFavorite: false,
			postInfo: {
				picture_link: []
			},
			including_service: "",
			guider: {},
			page: 0,
			pageCount: 0,
			posts: [],
			autheticate: {
				method: "GET",
				mode: "cors",
				credentials: "include",
				headers: {
					Accept: "application/json"
				}
			},
			alert: null,
			slideShow: [
				'/img/natural1.jpg',
				'/img/nature2.jpg',
				'/img/nature3.jpg',
				'/img/nature4.jpg',
				'/img/nature5.jpg',
				'/img/nature6.jpg',
			  ],
			  currentIndex: 0,
			  intervalId: null
		};
		this.onCancel = this.onCancel.bind(this);
		this.alertAccount = this.alertAccount.bind(this);
		this.onLogin = this.onLogin.bind(this);
	}
	
	async componentDidMount() {
		$("html").animate({ scrollTop: 0 }, 500, "swing");
		

		try {
			const post_id = this.props.match.params.post_id;
			let { page, autheticate, pageCount } = this.state;
			const response2 = await fetch(
				Config.api_url + "Guider/guiderByPostId?post_id=" + post_id,
				autheticate
			);
			if (!response2.ok) {
				window.location.href = '/page404';
				throw Error(response2.status + ": " + response2.statusText);
			}

			const guider = await response2.json();

			this.setState({ guider });

			//lay ra thông tin bài post đó

			const response = await fetch(
				Config.api_url + "guiderpost/findSpecificPost?post_id=" + post_id,
				autheticate
			);
			if (!response.ok) {
				throw Error(response.status + ": " + response.statusText);
			}

			//lay ra tất cả các bài post của guider đó

			const responsePosts = await fetch(
				Config.api_url +
				"guiderpost/postOfOneGuider/" +
				guider.guider_id +
				"/" +
				page,
				autheticate
			);

			if (!responsePosts.ok) {
				throw Error(responsePosts.status + ": " + responsePosts.statusText);
			}
			//lấy total page

			const totalPage = await fetch(
				Config.api_url +
				"guiderpost/postOfOneGuiderPageCount/" +
				guider.guider_id,
				autheticate
			);

			if (!totalPage.ok) {
				throw Error(totalPage.status + ": " + totalPage.statusText);
			}
			pageCount = await totalPage.json();

			//lấy ra category
			const responseCategories = await fetch(
				Config.api_url + "category/findAll",
				autheticate
			);
			if (!responsePosts.ok) {
				throw Error(responsePosts.status + ": " + responsePosts.statusText);
			}

			const postInfo = await response.json();
			const posts = await responsePosts.json();
			let link_youtube = postInfo.video_link;
			this.setState({
				postInfo: postInfo,
				posts: posts,
				guider: guider,
				pageCount,
				slideShow:postInfo.picture_link
			});
			if (link_youtube.includes("youtu.be")) {
				link_youtube = link_youtube.replace("youtu.be", "youtube.com/embed");
				this.setState({ link_youtube });
			} else {
				link_youtube = link_youtube.split("&");
				this.setState({
					link_youtube: link_youtube[0].replace("watch?v=", "embed/")
				});
			}
			this.including_service();
			let saved = await fetch(
				Config.api_url +
				"Traveler/saved?traveler_id=" + this.props.user.id
				+ "&post_id=" + this.props.match.params.post_id,
				this.state.autheticate
			);
			if(!saved.ok) throw new Error(saved.status + ": " + saved.statusText);
			this.setState({ statusFavorite: true });
			
		} catch (err) {
			console.log(err);
		}
		this.setupInterval();
	}
	
// slide
	timer = () => {
		// setState method is used to update the state
		this.commonNext();
	 }

	commonNext = () => {
		let { currentIndex, slideShow } = this.state;
		currentIndex++;
		if (currentIndex >= slideShow.length) {
			currentIndex = 0;
		}
		this.setState({ currentIndex });
	};

	handleNext = () => {
		clearInterval(this.state.intervalId);
		this.commonNext();
		this.setupInterval();
	};

	handlePrev = () => {
		clearInterval(this.state.intervalId);
		let { currentIndex, slideShow } = this.state;
		currentIndex--;
		if (currentIndex < 0) {
			currentIndex = slideShow.length - 1;
		}
		this.setState({ currentIndex });
		this.setupInterval();
	};

	handleChoose = (index) => {
		clearInterval(this.state.intervalId);
		this.setState({ currentIndex: index });
		this.setupInterval();
	  }

	setupInterval = () => {
		let intervalId = setInterval(this.timer, 3000);
		this.setState({ intervalId: intervalId });
	};
	// end slide

	handleGotoPage = (post_id) => {
		this.props.history.push("/post/" + post_id);
		window.location.reload();
		window.scrollTo(0, 0);
	};

	including_service = () => {
		let { postInfo } = this.state;
		let arr = postInfo.including_service;
		console.log('including'+arr);
		let including_service = "";
		for (let i = 0; i < arr.length; i++) {
			if (i === arr.length - 1) {
				including_service += arr[i];
			} else {
				including_service += arr[i] + ",";
			}
		}
		this.setState({ including_service });
	};

	loadPost = async (autheticate, page) => {
		let { guider } = this.state;

		const response = await fetch(
			Config.api_url + "guiderpost/allPostOfOneCategory/" + guider.guider_id + "/" + page,
			autheticate);

		if (!response.ok) {
			throw Error(response.status + ": " + response.statusText);
		}
		const posts = await response.json();
		this.setState({ posts });
	}

	handleCurrentPage = currentPage => {
		let { autheticate } = this.state;
		this.loadPost(autheticate, currentPage);
		this.setState({
			page: currentPage
		});
	};

	 handleFavorite = async () => {
		let { statusFavorite } = this.state;
		if (this.props.user.role !== 'TRAVELER') {
			this.alertAccount();
		} else if (!statusFavorite) {
			try {
				let save = await fetch(
					Config.api_url +
					"Traveler/favorite?traveler_id=" + this.props.user.id
					+ "&post_id=" + this.props.match.params.post_id,
					this.state.autheticate
				);
				if(!save.ok) throw new Error(save.status + ": " + save.statusText);
				this.setState({ statusFavorite: !statusFavorite });
			} catch (err) {
				console.log(err);
			}
		} else {
			try {
				let save = await fetch(
					Config.api_url +
					"Traveler/unlike?traveler_id=" + this.props.user.id
					+ "&post_id=" + this.props.match.params.post_id,
					this.state.autheticate
				);
				if(!save.ok) throw new Error(save.status + ": " + save.statusText);
				this.setState({ statusFavorite: !statusFavorite });
			} catch (err) {
				console.log(err);
			}
		}

	}
	onCancel() {
		this.setState({
			alert: null
		});
	}
	onLogin() {
		this.setState({ alert: null });
		$('.loginForm').show();
	}

	alertAccount() {
		const getAlert = () => (
			<div className="alertLogin">
				<SweetAlert
				warning
				showCancel
				confirmBtnText="Go to login"
				confirmBtnBsStyle="danger"
				title="Login notification"
				onConfirm={() => this.onLogin()}
				onCancel={() => this.onCancel()}
				focusCancelBtn
			>
				You are not traveler. Please login as traveler to save this tour!!
  			</SweetAlert>
			</div>
		);
		this.setState({
			alert: getAlert()
		});
	}

	range = (start, end) => {
		var ans = [];
		for (let i = start; i <= end; i++) {
			ans.push(i);
		}
		return ans;
	};

	render() {
		const { postInfo, pageCount, page, statusFavorite } = this.state;
		const post_id = this.props.match.params.post_id;
		let guider_id = this.state.guider.guider_id;
		//pagination
		const data = this.state.posts;
		const range = this.range(0, pageCount - 1);
		let renderPageNumbers = pageCount === 1 ? '' : range.map(i => (
			<button
				key={i}
				id={i}
				onClick={() => this.handleCurrentPage(i)}
				className={page === i ? "currentPage" : ""}
			>
				{i + 1}
			</button>
		));

		let posts = data.map((post, index) => (
	
				<li key={index} onClick={()=>this.handleGotoPage(post.post_id)}>
				<div className="sheet">
					<div className="imageFigure">
						<img src={post.picture_link[0]} alt="logo" />
					</div>
					<div className="experienceCard-details">
						<h3 className="postTitle" style={{color:'black',textDecoration:'none'}}>
							<span>
								{post.title}
							</span>
						</h3>
						<div className="price">
							<i className="fa fa-money" aria-hidden="true"></i>
							<span>{" " + post.price}$</span>
							<span className="experienceCard-topDetails-bullet">
								{" "}
								&#9679;{" "}
							</span>
							<i className="fa fa-hourglass-half" aria-hidden="true"></i>
							<span className="experienceCard-topDetails-duration">
								{" " + post.total_hour} hours

              </span>
							<span className="experienceCard-topDetails-bullet">
								{" "}
								&#9679;{" "}
							</span>
							{post.total_hour > 24 ? (
								<span>
									<i className="fa fa-moon-o" aria-hidden="true"></i>
									<span data-translatekey="Experience.SubcategoryOrTag.day-trip">
										{" "}
										Long trip
                  </span>
								</span>
							) : (
									<span>
										<i className="fa fa-sun-o" aria-hidden="true"></i>
										<span data-translatekey="Experience.SubcategoryOrTag.day-trip">
											{" "}
											Day trip
                  </span>
									</span>
								)}
						</div>
						<div className="experienceCard-bottomDetails">
							<Rated number={post.rated} />
						</div>
					</div>
				</div>
			</li>
		));
		let imgPostInfo = (
			<div className="slideshow-container">
				<h2>What you can expect</h2>
				{postInfo.picture_link.map((link, i) => (
					<div
						className={`mySlides${
							i === this.state.currentIndex ? " active" : ""
							}`}
						key={i}
					>
						<img src={link} />
					</div>
				))}
				<a className="prev" onClick={this.handlePrev}>
					❮
        </a>
				<a className="next" onClick={this.handleNext}>
					❯
        </a>
			</div>
		);
		//<img className="imgPostInfo" src={postInfo.picture_link} />;
		const favorite = statusFavorite ? "favorite" : '';
		return (
			<div>
				{this.state.alert}
				<div>
					<div id="reactContainer">
						{/*  Content  */}
						<div className="content">
							<div className="content-left">
								{guider_id ? (
									<GuiderInPost
										guiderId={guider_id}
										postId={this.props.match.params.post_id}
									/>
								) : null}
							</div>
							<div className="content-right">
								<div className="PostDetail">
									<div className="intro">
										<iframe
											src={this.state.link_youtube}
											frameBorder="0"
											allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
											allowFullScreen
										></iframe>
										<h2 className="titleTour">{postInfo.title}<i className="fa fa-heart" id={favorite} onClick={this.handleFavorite} aria-hidden="true"></i></h2>
										<p className="introduceTour">{postInfo.description}</p>
									</div>
									<div className="activities">
										<ul>
											<li>
												<i className="fa fa-thumb-tack" aria-hidden="true"></i>
												<span>{postInfo.location}</span>
											</li>
											<li>
												<i className="fa fa-cutlery" aria-hidden="true"></i>
												<span>{postInfo.category} tour</span>
											</li>
											<li>
												<i className="fa fa-hourglass-end" />
												<span>{postInfo.total_hour} hours</span>
											</li>

											{postInfo.total_hour > 24 ? (
												<li>
													<i className="fa fa-moon-o" aria-hidden="true"></i>
													<span>Long trip</span>
												</li>
											) : (
													<li>
														<i className="fa fa-sun-o" aria-hidden="true"></i>
														<span>Day trip</span>
													</li>
												)}

											<li>
												<i className="fa fa-users" />
												<span>Private tour. Only you and your host</span>
											</li>
										</ul>
										<i className="fa fa-sticky-note" />
										<span>
											<strong>Including:</strong>
											{this.state.including_service}
										</span>
									</div>

									<ReviewInPost postId={post_id} />
									<PlanInPost postId={post_id} />
									<div dangerouslySetInnerHTML={{__html: postInfo.reasons}} />
								</div>
								{/* slideshow */}
									<div className="slideshow">
										<div className="slideshow-container">
										<h2>What you can expect</h2>
										{this.state.slideShow.map((link, i) => (
											<div className={`mySlides${i === this.state.currentIndex ? ' active' : ''}`} key={i}>
											<img src={link} />
											</div>
										))}
										<a className="prev" onClick={this.handlePrev}>
											❮
										</a>
										<a className="next" onClick={this.handleNext}>
											❯
										</a>
										</div>
										<br />
										<div
										className="navigation"
										style={{ textAlign: "center", marginBottom: 30 }}
										>
										{this.state.slideShow.map((link, i) => (
										<span className={`dot${i === this.state.currentIndex ? ' active' : ''}`} key={i} onClick={() => this.handleChoose(i)} />
										))}
										</div>
									</div>
									{/* end slideshow */}
								<div className="bookOffers">
									<h2 style={{ marginBottom: "20px" }}>Watch one of my trip</h2>
									<ul>{posts}</ul>
								</div>

								<div className="pagination">
									<div className="paginationContent">{renderPageNumbers}</div>
								</div>
							</div>
						</div>
					</div>
					{/*  End Content  */}
				</div>
			</div>
		);
	}

}
function mapStateToProps(state) {
	const user = state.user;

	return { user };
}
export default connect(mapStateToProps)(PostDetail);
