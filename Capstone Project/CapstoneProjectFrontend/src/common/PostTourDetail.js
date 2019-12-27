import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import "font-awesome/css/font-awesome.min.css";
import Config from '../Config';
import Rated from '../Guider/Rated';
import TopGuider from '../Guider/TopGuider';
class PostTourDetail extends Component {
	constructor(props) {
		super(props);
		this.state = {
			dataPostOneCategory: [],
			posts: [],
			postsContribute: [],
			locations: [],
			page: 0,
			pageCount:0,
			autheticate: {
				method: "GET",
				mode: "cors",
				credentials: "include",
				headers: {
					'Accept': 'application/json'
				}
			}
		};
	}

	async componentDidMount() {
		let {page,pageCount,autheticate} = this.state;
		try {
			const response = await fetch(
				Config.api_url + "location/findAll",
				autheticate);
			if (!response.ok) {
				throw Error(response.status + ": " + response.statusText);
			}

			const totalPage = await fetch(
				Config.api_url + "guiderpost/allPostOfOneCategoryPageCount/"+this.props.match.params.id,
				autheticate);
			if (!totalPage.ok) {
				throw Error(totalPage.status + ": " + totalPage.statusText);
			}
			pageCount = await totalPage.json();

			const locations = await response.json();
			this.setState({locations,pageCount});
			this.loadPost(autheticate,page);
		} catch (err) {
			console.log(err);
		}


	}
	loadPost = async (autheticate,page)=>{
		const responsePosts = await fetch(
			Config.api_url + "guiderpost/allPostOfOneCategory/" + this.props.match.params.id + "/" + page,
			autheticate);

		if (!responsePosts.ok) {
			throw Error(responsePosts.status + ": " + responsePosts.statusText);
		}
		const dataPostOneCategory = await responsePosts.json();
		this.setState({ dataPostOneCategory});
	}

	handleCurrentPage = (currentPage) => {
		let {autheticate} = this.state;
		this.loadPost(autheticate,currentPage);
		this.setState({
			page: currentPage
		});
	}

	range = (start, end) => {
		var ans = [];
		for (let i = start; i <= end; i++) {
			ans.push(i);
		}
		return ans;
	}

	render() {
		const data = this.state.dataPostOneCategory;
		const {locations,pageCount,page} = this.state;
		const range = this.range(0, pageCount - 1);
		let renderPageNumbers = pageCount === 1 ? '':
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
			)
		;

		let category_name = window.sessionStorage.getItem('category_name');

		let showTour = (
			<div className="postResult" style={{width:'100%'}}>
			<div className="bookOffers guiderResult" id="searchResult">	
				<ul id="tourDetailList" className="ulSearchLocation" style={{width:'100%'}}>
					{
						this.state.dataPostOneCategory.map((post, index) => (
						<Link to={"/post/" + post.post_id}>
						<li key={index}>
							<div className="sheet" id="sheetInTour" style={{width:"400px"}}>
							<div className="imageFigure">
								<img src={post.picture_link[0]} alt="logo" />
							</div>
							<div className="experienceCard-details">
								<h3 style={{textDecoration:'none',color:'black'}}>
								<span
									// onClick={() => this.handleGotoPage(post.post_id, guider_id)}
								>
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
						</Link>
					))
					}
				</ul>
			</div>
		</div>
		)

		return (
			<div>
				<div className='postInTour'>
					<h2 style={{marginLeft:'3%'}}>All trips about {category_name} </h2>
					<div className="contentTour" style={{width:'100%',margin:'0',paddingLeft:'2%'}}>
						{
							showTour
						}
					</div>
					<div className="pagination">
						<div className="paginationContent">
							{renderPageNumbers}
						</div>
					</div>
					<div className="categoryTour">
					<TopGuider />
				</div>
				</div>
			</div>
		);
	}
}

export default PostTourDetail;