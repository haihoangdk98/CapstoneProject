import { connect } from "react-redux";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import Config from '../Config'
import React, { Component } from "react";
import Rated from '../Guider/Rated'
class Favorite extends React.Component {
      constructor(props) {
            super(props);
            this.state = {
                  searchPost: [],

                  page: 0,
                  authenticate: {
                        method: "GET",
                        mode: "cors",
                        credentials: "include",
                        headers: {
                              'Accept': 'application/json'
                        },
                  }
            };
      }

      async componentDidMount() {
            try {
                  //let guider_id = this.props.id;
                  const responsePosts = await fetch(
                        Config.api_url + "Traveler/getFavList?traveler_id=" + this.props.user.id + "&page=" + 0,
                        this.state.authenticate
                  );
                  const pageCount = await fetch(
                        Config.api_url + "Traveler/getFavListPageCount?traveler_id=" + this.props.user.id,
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
                  this.setState({ searchPost: posts,page: totalPage });
            } catch (err) {
                  console.log(err);
            }
      }



       handleCurrentPage = async (currentPage) => {
            try {
                  //let guider_id = this.props.id;
                  const responsePosts = await fetch(
                        Config.api_url + "Traveler/getFavList?traveler_id=" + this.props.user.id + "&page=" + (+currentPage+1),
                        this.state.authenticate
                  );
                  const pageCount = await fetch(
                        Config.api_url + "Traveler/getFavListPageCount?traveler_id=" + this.props.user.id,
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
                  this.setState({ searchPost: posts, page: currentPage });
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

      render() {
            let { searchPost, page, totalPage } = this.state;
            let location = searchPost.length;

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
            return (
                  <div className="postResult" style={{minHeight:'700px'}}>
                        <div className="bookOffers guiderResult" id="searchResult">
                              <div className="headerGuiderResult">
                                    <h2 className="h2SsearchResult">Your favorite posts</h2>
                                    <hr />
                              </div>

                              <ul className="ulSearchLocation">

                                    {this.state.searchPost.map((post, index) => (
                                          <Link to={"/post/" + post.post_id}>
                                                <li key={index}>
                                                      <div className="sheet">
                                                            <div className="imageFigure">
                                                                  <img src={post.picture_link[0]} alt="logo" />
                                                            </div>
                                                            <div className="experienceCard-details">
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
      }
}
function mapStateToProps(state) {
      const user = state.user;
      return { user };
}
export default connect(mapStateToProps)(Favorite);
