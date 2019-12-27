import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import Rated from "./Rated";
import GuiderInPost from "./GuiderInPost";
import EditPost from "./EditPost";
import Config from "../Config";
import { connect } from "react-redux";
class ManagePost extends Component {
  constructor(props) {
    super(props);

    this.state = {
      currentPage: 1,
      todosPerPage: 8,
      posts: [],
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
  }

  async componentDidMount() {
    //console.log(this.props);
    try {
      let { cors } = this.state;
      let guider_id = this.props.user.id;
      const responsePosts = await fetch(
        Config.api_url +
          "guiderpost/postOfOneGuiderManage/" +
          guider_id +
          "/" +
          this.state.page,
        cors
      );

      if (!responsePosts.ok) {
        throw Error(responsePosts.status + ": " + responsePosts.statusText);
      }

      const response = await fetch(
        Config.api_url +
          "guiderpost/postOfOneGuiderManagePageCount/" +
          guider_id,
        cors
      );
      if (!response.ok) {
        throw Error(response.status + ": " + response.statusText);
      }
      const totalPage = await response.json();
      const posts = await responsePosts.json();

      this.setState({ posts: posts, totalPage });
    } catch (err) {
      console.log(err);
    }
  }

  handleCurrentPage = async event => {
    let { cors } = this.state;
    let guider_id = this.props.user.id;
    const responsePosts = await fetch(
      Config.api_url +
        "guiderpost/postOfOneGuiderManage/" +
        guider_id +
        "/" +
        this.state.page,
      cors
    );

    if (!responsePosts.ok) {
      throw Error(responsePosts.status + ": " + responsePosts.statusText);
    }
    const posts = await responsePosts.json();
    this.setState({ posts: posts });
    this.setState({
      currentPage: event.target.id
    });
  };

  deactive = async eve => {
    eve.preventDefault();
    let post = Object.assign([], this.state.posts);
    try {
      post[eve.target.id].active = !post[eve.target.id].active;
      const responsePosts = await fetch(
        Config.api_url +
          "guiderpost/activeDeactivePost?post_id=" +
          eve.target.value,
        {
          method: "GET",
          mode: "cors",
          credentials: "include"
        }
      );
      if (!responsePosts.ok) {
        throw Error(responsePosts.status + ": " + responsePosts.statusText);
      }

      this.setState({ posts: post });
    } catch (err) {
      console.log(err);
    }
  };

  range = (start, end) => {
    var ans = [];
    for (let i = start; i <= end; i++) {
      ans.push(i);
    }
    return ans;
  };
  render() {
    let { totalPage, page } = this.state;
    //pagination
    const range = this.range(0, totalPage - 1);
    let renderPageNumbers =
      totalPage === 1
        ? ""
        : range.map(i => (
            <button
              key={i}
              id={i}
              onClick={() => this.handleCurrentPage(i)}
              className={page === i ? "currentPage" : ""}
            >
              {i + 1}
            </button>
          ));
          
    //console.log(this.props);
    let order = this.state.posts.map((post, index) => (
      <tr className="row100 body" key={index}>
        <td className="cell100 column1">
          <Link to={`/post/${post.post_id}`}>{post.title}</Link>
        </td>

        <td className="cell100 column2">
          <div>
            <Link to={"/update/" + this.props.guiderId + "/" + post.post_id}>
              <button
                className="accept btn btn-primary btn-icon-split"
                value={post.post_id}
                id={index}
                type="button"
              >
                Edit
              </button>{" "}
            </Link>
            {post.active ? (
              <button
                onClick={this.deactive}
                value={post.post_id}
                id={index}
                className="refuse btn btn-danger btn-icon-split "
                type="button"
                style={{ marginLeft: "10px" }}
              >
                De Activate
              </button>
            ) : (
              <button
                onClick={this.deactive}
                value={post.post_id}
                id={index}
                className="active btn btn-secondary btn-icon-split "
                type="button"
                style={{ marginLeft: "10px" }}
              >
                Activate
              </button>
            )}
          </div>
        </td>
      </tr>
    ));

    return (
      <div className="tvlManager_Container">
        <div className="table100 ver1">
          <div className="wrap-table100-nextcols js-pscroll ps ps--active-x">
            <div className="table100-nextcols">
              <table style={{ margin: "0 auto", width: "100%" }}>
                <thead>
                  <tr className="row100 head">
                    <th className="cell100 column1" style={{ width: "560px" }}>
                      Post
                    </th>

                    <th className="cell100 column2" style={{ width: "260px" }}>
                      Action
                    </th>
                  </tr>
                </thead>
                <tbody>{order}</tbody>
              </table>
            </div>
            <div className="pagination" style={{ marginTop: "20px" }}>
              <div className="paginationContent">{renderPageNumbers}</div>
            </div>
            <div className="wrap-table100-nextcols js-pscroll"></div>
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
export default connect(mapStateToProps)(ManagePost);
