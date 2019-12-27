import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import Rated from "./Rated";
import GuiderInPost from './GuiderInPost';
import EditPost from './EditPost';
import Config from '../Config';
import { connect } from 'react-redux';
class ManagePost extends Component {
      constructor(props) {
            super(props);
            console.log("constructor pót");
		console.log(props);
            this.state = {
                  currentPage: 1,
                  todosPerPage: 8,
                  posts: [],
                  page: 0
            };
      }
      // componentWillMount() {
      //       console.log(this.props);
	// 	var user = JSON.parse(sessionStorage.getItem('user'));
	// 	if (user !== null) {
	// 		console.log("session storage");
	// 	} 
	// }

      async componentDidMount() {
            //console.log(this.props);
            try {
                  let guider_id = this.props.user.id;
                  const responsePosts = await fetch(
                        Config.api_url + "guiderpost/postOfOneGuider/" + guider_id+"/"+this.state.page,
                        {
                              method: "GET",
                              mode: "cors",
                              credentials: "include",
                              headers: {
                                    'Accept': 'application/json'
                              },
                        }
                  );

                  if (!responsePosts.ok) {
                        throw Error(responsePosts.status + ": " + responsePosts.statusText);
                  }

                  const posts = await responsePosts.json();

                  this.setState({ posts: posts, page: ++this.state.page });
            } catch (err) {
                  console.log(err);
            }
      }

      handleCurrentPage = (event) => {
            this.setState({
                  currentPage: event.target.id
            });
      }

      render() {
            console.log(this.props);

            let posts = this.state.posts.map((post, index) => (
                  <li key={index}>
                        <Link to={"/update/"+this.props.guiderId+"/" + post.post_id}>
                        <div className="sheet">
                              <div className="imageFigure">
                                    <img src={post.picture_link[0]} alt="logo" width="42" height="42" />
                              </div>
                              <div className="experienceCard-details">
                                    <span className="enjoy">
                                          Enjoy <span className="withName">{post.post_id}</span>
                                    </span>
                                    <h3>
                                          {post.title}
                                    </h3>

                              </div>
                        </div>
                        </Link>
                  </li>
            ));
            // let routes = this.state.posts.map((post, index) => (
            //       <Route path={"/update/:guider/:post"} key={index} component={EditPost} />
                       


            // ));

 //<EditPost guiderId={this.props.guiderId} postId={post.post_id} /></Route> 
            return (
                  <div>
                        {/* <Switch>
                              {routes}
                        </Switch> */}
                        <div>

                              <div id="reactContainer">
                                    {/*  Content  */}
                                    <div className="content">
                                          <div className="content-left">

                                          </div>
                                          <div className="content-right">
                                                <div className="bookOffers">
                                                      <h2>Book one of my offers in Ha Noi</h2>
                                                      <ul>{posts}</ul>
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
      return {user};
}
export default connect(mapStateToProps)(ManagePost);
