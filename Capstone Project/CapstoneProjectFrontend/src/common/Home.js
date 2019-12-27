import React, { Component } from "react";
import Config from "../Config";
import SweetAlert from "react-bootstrap-sweetalert";
import { Link } from "react-router-dom";
import $ from "jquery";
import Rated from '../Guider/Rated';
import TopGuider from "../Guider/TopGuider";
// import ItemsCarousel from 'react-items-carousel';
import { NONAME } from "dns";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

const responsive = {
	superLargeDesktop: {
		// the naming can be any, depends on you.
		breakpoint: { max: 4000, min: 3000 },
		items: 5,
	},
	desktop: {
		breakpoint: { max: 3000, min: 1024 },
		items: 3,
		slidesToSlide: 3, // optional, default to 1.
	},
	tablet: {
		breakpoint: { max: 1024, min: 464 },
		items: 2,
		slidesToSlide: 2, // optional, default to 1.
	},
	mobile: {
		breakpoint: { max: 464, min: 0 },
		items: 1,
		slidesToSlide: 1, // optional, default to 1.
	},
};
class Home extends Component {

	constructor(props) {
		super(props);

		this.state = {
			category: [],
			alert: null,
			tours: [],
			searchGuider: [],
			searchPost: [],
			slideShow: [
				"images/hoguom.jpg",
				"images/buncha.jpg",
				"images/dinhdoclap.jpg",
				"images/hanoi.jpg",
				"images/phobo.jpg",
				"images/thanhphodanang.jpg"
			],
			currentIndex: 0,
			filter: "none",
			location: [],
			page: 0,
			guiderPage: 0,
			authenticate:{
				method: "GET",
				mode: "cors",
				credentials: "include",
				headers: {
					'Accept': 'application/json'
				},
			}
		};
	}


	onNotification() {
		this.setState({ alert: null });
		sessionStorage.setItem("messagePay", "");
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
		let {authenticate} = this.state;
		$(".search-4ul6i").focus(function () {
			$(".fillter-4ul6i").show();
		});
		$(document).mouseup(function (e) {
			if (
				!$(".Search-3ul6i").is(e.target) &&
				!$(".fillter-4ul6i").is(e.target) &&
				$(".Search-3ul6i").has(e.target).length === 0 &&
				$(".fillter-4ul6i").has(e.target).length === 0
			) {
				$(".fillter-4ul6i").hide();
			}
		});

		try {
			const locate = await fetch(Config.api_url + "location/findAll", authenticate);
			if (!locate.ok) {
				throw Error(responseTour.status + ": " + responseTour.statusText);
			}
			const cities = await locate.json();


			const responseTour = await fetch(Config.api_url + "guiderpost/getTopTour", authenticate);
			if (!responseTour.ok) {
				throw Error(responseTour.status + ": " + responseTour.statusText);
			}

			const responsePosts = await fetch(Config.api_url + "category/findAll");
			if (!responsePosts.ok) {
				throw Error(responsePosts.status + ": " + responsePosts.statusText);
			}

			const category = await responsePosts.json();
			const tours = await responseTour.json();
			this.setState({ category: category, tours: tours, location: cities });
		} catch (err) {
			console.log(err);
		}
		if (sessionStorage.getItem("messagePay")) {
			var messagePay = sessionStorage.getItem("messagePay");
			if (messagePay === "Error user or tour inf") {
				this.notification(
					"You are not logged in. Please login or register to use service mywebsite!!"
				);
			} else if (messagePay === "You are Guider") {
				this.notification("Guider do not have access to here");
			} else if (messagePay === "You are Traveler") {
				this.notification("Traveler do not have access to here");
			}
		}

		this.setupInterval();

		// window.onscroll = function () {
		// 	if (window.pageYOffset === 0) {
		// 		$("#navbar").css({ background: "none", "border-bottom": "none" });
		// 		$(".navbarRightContent ul li").css({
		// 			color: "black",
		// 			"font-size": "18px"
		// 		});
		// 	}
		// };
	}

	setupInterval = () => {
		let intervalId = setInterval(this.commonNext, 5000);
		this.setState({ intervalId: intervalId });
	};

	componentWillUnmount() {
		clearInterval(this.state.intervalId);
		window.onscroll = null;
	}

	commonNext = () => {
		let { currentIndex, slideShow } = this.state;
		currentIndex++;
		if (currentIndex >= slideShow.length) {
			currentIndex = 0;
		}
		this.setState({ currentIndex });
	};

	handleChange = category_name => {
		window.sessionStorage.setItem("category_name", category_name);
	};

	searchGuider = async (input,page) => {
		try {
				const responsePosts = await fetch(
					Config.api_url + "Guider/Search/" + input + "/" + page,
					this.state.authenticate
				);
				const pageCount = await fetch(
					Config.api_url + "Guider/SearchPageCount/" + input ,
					this.state.authenticate
				);
	
				if (!responsePosts.ok) {
					throw Error(responsePosts.status + ": " + responsePosts.statusText);
				}
				
				const guiders = await responsePosts.json();
				const totalPage = await pageCount.json();
				console.log(guiders);
				console.log(totalPage);
				
				this.setState({ searchGuider: guiders,inputSearch:input,totalPage});
			
		} catch (err) {
			console.log(err);
		}
	}

	searchLocation = async (input,page) => {
		try {
			//let guider_id = this.props.id;
			const responsePosts = await fetch(
				Config.api_url + "guiderpost/findAllPostWithLocationName/" + input + "/" + page,
				this.state.authenticate
			);
			const pageCount = await fetch(
				Config.api_url + "guiderpost/findAllPostWithLocationNamePageCount/" + input ,
				this.state.authenticate
			);

			if (!responsePosts.ok) {
				throw Error(responsePosts.status + ": " + responsePosts.statusText);
			}
			if (!pageCount.ok) {
				throw Error(pageCount.status + ": " + pageCount.statusText);
			}
			let totalPage = await pageCount.json();
			const posts = await responsePosts.json();
			this.setState({ searchPost: posts,inputSearch:input,totalPage});
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
		let {inputSearch,filter} = this.state;
		if(filter === "guider"){
			this.searchGuider(inputSearch,currentPage);
		}else if(filter === "location"){
			this.searchLocation(inputSearch,currentPage);
		}
		
		this.setState({
			page: currentPage
		});
		
	}

	 notFound = () =>{
		 return <div className="searchNotFound">
			 <h2 style={{fontWeight:'600'}}>No results</h2>
			 <p>Your search did not return any results.</p>
		 </div>
	 }

	render() {
		let input = null;
		let { currentIndex, slideShow,inputSearch,searchGuider,searchPost,page,totalPage } = this.state;
		let guiderlength = searchGuider.length;
		let location = searchPost.length;
		let src = Config.api_url + slideShow[currentIndex];
		
		//pagination
		const range = this.range(0, totalPage - 1);
		let renderPageNumbers = totalPage === 1 ? '' :
		range.map((i) => (
			<button
				key={i}
				id={i}
				onClick={()=>this.handleCurrentPage(i)}
				className={page === i ? "currentPage" : ''}
			>
				{i+1}
			</button>
			)
		);

		var settings = {
			dots: false,
			arrows: true,
			infinite: true,
			autoplay: true,
			speed: 500,
			slidesToShow: 4,
			slidesToScroll: 1,
			responsive: [
			{
				breakpoint: 1025,
				settings: {
				slidesToShow: 3,
				slidesToScroll: 1,
				infinite: true,
				dots: true
				}
			},
			{
				breakpoint: 600,
				settings: {
				slidesToShow: 2,
				slidesToScroll: 2
				}
			},
			{
				breakpoint: 480,
				settings: {
				slidesToShow: 1,
				slidesToScroll: 1
				}
			}
			// You can unslick at a given breakpoint now by adding:
			// settings: "unslick"
			// instead of a settings object
			]
		};

		let tour = this.state.category.map((tour, index) => {
			return (
				<div className="setImgSlide" key={index}>
					<img src={tour.image} />
					<Link
						to={"/posttour/" + tour.category_id}
						onClick={() => {
							this.handleChange(tour.category);
						}}
					>
						<button className="categoriesTour">{tour.category} tour</button>
					</Link>
				</div>
			);
		});

		let slide = this.state.tours.map((value, index) => (
			<div className="slideContent" key={index}>

				<div className="bg_Gradient">
					<h2>Enjoy our {value.title}</h2>
					<img src={value.picture_link[0]} />
					<Link to={"/post/" + value.post_id}>
						<button>Explore</button>
					</Link>
				</div>
			</div>
		));
		let home = (<div className="categoryTour">
			<div className="containerMain">
				<h1>Explore Withlocals</h1>
				<h2 className="sectionSubtitle">
					<span data-translatekey="Homepage.Categories.subTitle">
						All of our tours and activities are:{" "}
						<span>• Private • Personalized • </span>{" "}
						<span>With the local of your choice</span>
					</span>
				</h2>
				<div className="tourDetail"><Slider {...settings}>
					{tour}
				</Slider></div>
				{this.state.alert}
				<h1>The travel is most appreciated</h1>
				<div className="coverTopTour">{slide}</div>
				<TopGuider />
			</div>
		</div>);

		let postResult = (<div className="postResult">
			<div className="bookOffers guiderResult" id="searchResult">
				<div className="headerGuiderResult">
					<h2 className="h2SsearchResult">Search Results</h2>
					<hr />
					<h3>All results related to <span className="spanSearhResult">{"'" + inputSearch + "'"}</span></h3>
				</div>

				<ul className="ulSearchLocation">
					{
						location === 0 ? this.notFound()
							:
							this.state.searchPost.map((post, index) => (
								<Link to={"/post/" + post.post_id}>
									<li key={index}>
										<div className="sheet">
											<div className="imageFigure">
												<img src={post.picture_link[0]} alt="logo" />
											</div>
											<div className="experienceCard-details">
												{/* <span className="enjoy">
								Enjoy <span className="withName">{post.title}</span>
								</span> */}
												<h3 className="h3PostResult">
													<span>
														{post.title}
													</span>
												</h3>
												<div className="price">
													<i className="fa fa-money" aria-hidden="true"></i><span>{" " + post.price}$</span>
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
													{
														post.total_hour > 24 ?
															<span>
																<i className="fa fa-moon-o" aria-hidden="true"></i>
																<span data-translatekey="Experience.SubcategoryOrTag.day-trip">
																	{" "}Long trip
								</span>
															</span>
															:
															<span>
																<i className="fa fa-sun-o" aria-hidden="true"></i>
																<span data-translatekey="Experience.SubcategoryOrTag.day-trip">
																	{" "} Day trip
								</span>
															</span>
													}

												</div>
												<div className="experienceCard-bottomDetails">
													<Rated number={post.rated} />
												</div>
											</div>
										</div>
									</li>
								</Link>
							))
					}
				</ul>
			</div>
			<div className="pagination">
				<div className="paginationContent">
					{renderPageNumbers}
				</div>
			</div>
		</div>
		);
		
		let guiderResult = (
			<div className="guiderResult">
				<div className="headerGuiderResult">
					<h2 className="h2SsearchResult">Search Results</h2>
					<hr />
					<h3>All results related to <span className="spanSearhResult">{"'" + inputSearch + "'"}</span></h3>
				</div>
				<div className="topGuider" id="SearchTopGuider">
					<div className="topGuiderByRate">
						<div className="content-left" id="searchGuider">
							{guiderlength === 0 ? this.notFound()
								:
								this.state.searchGuider.map((post, index) => (
									<div className="profile-box" key={index}>
										<div className="pb-header header-stick">
											<div className="header-pb">
												<h1 className="TitlePb TileStickyPb searchGuiderName">
													{post.first_name + "" + post.last_name}
												</h1>
											</div>
										</div>
										<img src={post.avatar} className="imgpb-header"></img>
										<Rated number={post.rated} />
										<Link to={"/guider/" + post.guider_id}>
											<button className="contactMe">Contact Me</button>
										</Link>
									</div>
								))
							}
						</div>
					</div>
				</div>
				<div className="pagination">
				<div className="paginationContent">
					{renderPageNumbers}
				</div>
			</div>
			</div>
		);

		let visible = (this.state.filter === "none") ? home : (this.state.filter === "guider") ? guiderResult : postResult;
		return (
			<div>
				<div className="homeSlide">
					<img src={src} />
					<div className="Title-e9h41">
						<h1>Book unique private tours and activities with locals worldwide</h1>
						<div className="Search-2ul6i">
							<div className="Search-3ul6i">
								<label>
									<input
										type="text"
										placeholder="Where do you want to go?"
										name="search"
										autoComplete="off"
										className="search-4ul6i"
										ref={node => { input = node; }}
									/>
								</label>
								<div className="fillter fillter-4ul6i">
									<div className="filter-Content">
										<div className="localsOrExperience">
											<h3 className="explore">Explore TravelWlocals</h3>
											<div className="button-group">
												<button className="active" onClick={(eve) => {
													eve.preventDefault();
													this.state.filter = "guider";
												}}>Guider</button>
												<button onClick={(eve) => {
													eve.preventDefault();
													this.state.filter = "location";
												}}>Location</button>
											</div>
										</div>
									</div>
								</div>
							</div>
							<button className="Button-2i" onClick={(eve) => {
								eve.preventDefault();
								if (!input.value.trim()) {
									this.setState({inputSearch:''});
									return ;
								}
								this.state.filter = (this.state.filter === "none") ? "guider" : this.state.filter;
								if (this.state.filter === "guider") {
									this.searchGuider(input.value,0);
								} else if (this.state.filter === "location") {
									this.searchLocation(input.value,0);
								} else {
									console.log("search other filter");
								}

							}}>Search</button>

						</div>
					</div>
				</div>
				{visible}
			</div>
		);
	}
}


export default Home;
