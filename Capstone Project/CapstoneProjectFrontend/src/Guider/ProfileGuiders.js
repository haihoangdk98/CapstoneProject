import React, { Component } from 'react';
import "font-awesome/css/font-awesome.min.css";
import $ from 'jquery';
import GuiderInPost from './GuiderInPost';
import Config from '../Config';
import { Link } from 'react-router-dom';
import Rated from './Rated';
import GuiderInShort from './GuiderInShort';
class ProfileGuiders extends Component {
  constructor(props) {
    super(props);
    this.state = {
      guider_id: 0,
      posts: [],
      guider: {
      },
      page: 0,
      totalPage: 0,
      autheticate: {
        method: "GET",
        mode: "cors",
        credentials: "include",
        headers: {
          Accept: "application/json"
        }
      },

      about_me:[""],
      hide:true
    }
  }
  componentDidMount() {
    //add css with jquery
    $("head").append('<link href="/css/profile.css" rel="stylesheet"/>');
    $("head").append('<link href="/css/login.css" rel="stylesheet"/>');


    // read more and read less
    $('.moreless-button').click(function () {
      $('.moretext').slideToggle();
      if ($('.moreless-button').text() === "Read more") {
        $(this).text("Read less")
      } else {
        $(this).text("Read more")
      }
    });

    // //show and hide comment
    // var size_li = $(".listReview li").length;
    // $('#showLess').hide();
    // var x = 3;
    // $('.listReview li:lt(' + x + ')').show();

    // $('#loadMore').click(function () {
    //   x = (x + 3 <= size_li) ? x + 3 : size_li;
    //   $('.listReview li:lt(' + x + ')').show(500);
    //   if (x > 3) {
    //     $('#showLess').show();
    //   }
    //   if (x === size_li) {
    //     $('#loadMore').hide();
    //   }
    // });
    // $('#showLess').click(function () {
    //   var x = (x - 3 < 3) ? 3 : x - 3;
    //   $('.listReview li').not(':lt(' + x + ')').hide(500);
    //   if (x <= 3) {
    //     $('#showLess').hide();
    //   } if (x < size_li) {
    //     $('#loadMore').show();
    //   }
    // });

    //get guider id
    let guider_id = this.props.match.params.guider_id;
    this.loadInfoGuider(guider_id);
    this.LoadPostGuider(guider_id, this.state.page);
    this.setState({ guider_id });
  }


  loadInfoGuider = async (guider_id) => {
    let { autheticate } = this.state;
    try {
      const response = await fetch(
        Config.api_url + "Guider/" + guider_id,
        autheticate
      );
      if (!response.ok) {
        window.location.href = '/page404';
        throw Error(response.status + ": " + response.statusText);
      }

      const guider = await response.json();
      const about_me = guider.about_me.split(".");
      if (guider.profile_video.includes("youtu.be")) {
        guider.profile_video = guider.profile_video.replace("youtu.be", "youtube.com/embed");
      } else {
        guider.profile_video = guider.profile_video.split("&");
        guider.profile_video = guider.profile_video[0].replace("watch?v=", "embed/");
      }
      // console.log(guider);
      this.setState({ guider, about_me });
    } catch (err) {
      console.log(err)
    }
  }

  LoadPostGuider = async (guider_id, page) => {
    let { autheticate, posts } = this.state;
    try {
      const response = await fetch(
        Config.api_url +
        "guiderpost/postOfOneGuider/" + guider_id +
        "/" +
        0,
        autheticate
      );
      const pageCount = await fetch(
        Config.api_url +
        "guiderpost/postOfOneGuiderPageCount/" + guider_id,
        autheticate
      );

      const totalPage = await pageCount.json();
      posts = await response.json();
      console.log('posts',posts);
      this.setState({ posts, totalPage })
    } catch (error) {
      console.log(error)
    }
  }

  RenderPostGuider = (guider_id) => {
    let { posts } = this.state;
    let post = posts.map((post, index) => (


      <li key={index}>
        <Link to={'/post/' + post.post_id} style={{ textDecoration: 'none' }}>
          <div className="sheet">
            <div className="imageFigure">
              <img src={post.picture_link[0]} alt="logo" />
            </div>
            <div className="experienceCard-details">
              <h3 style={{color:'black',textDecoration:'none'}}>
                <span
                //onClick={() => this.handleGotoPage(post.post_id, guider_id)}
                >
                  {post.title}
                </span>
              </h3>
              <div className="price" style={{ color: 'black' }}>
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
        </Link>
      </li>

    ));
    return post;
  }

  handleCurrentPage = currentPage => {
    let { guider_id } = this.state;
    this.LoadPostGuider(guider_id, currentPage);
    this.setState({
      page: currentPage
    });
  };

  range = (start, end) => {
    var ans = [];
    for (let i = start; i <= end; i++) {
      ans.push(i);
    }
    return ans;
  };

  render() {
    let { guider_id, guider, totalPage, page, about_me } = this.state;
    let post = this.RenderPostGuider(guider_id);
    console.log(post);
    const range = this.range(0, totalPage - 1);
    let renderPageNumbers = totalPage === 1 ? '' :
      range.map(i => (
        <button
          key={i}
          id={i}
          onClick={() => this.handleCurrentPage(i)}
          className={page === i ? "currentPage" : ""}
        >
          {i + 1}
        </button>
      ))
      ;

    return (
      <div>
        <div>
          <div id="reactContainer">
            {/* Menubar */}
            {/* End MenuBar */}
            {/*  Content  */}
            <div className="content">
              <div className="content-left">
                {guider_id ? (
                  <GuiderInShort
                    guiderId={guider_id}
                    postId={this.props.match.params.post_id}
                  />
                ) : null}
              </div>
              <div className="content-right">
                {/* intro content */}
                <div className="intro">
                  <h2>Hi There ! Nice to meet you</h2>
                  <iframe
                    src={guider.profile_video}
                    frameBorder="0"
                    allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                    allowFullScreen
                  ></iframe>
                  <div className="readMoreText">
                    <div id="section">
                      <div className="article">
                        <p>
                          {
                            about_me.slice(0, 4).map((value) => (
                              <span>{value}.</span>
                            ))
                          }
                        </p>
                       
                        <p className="moretext">
                          {

                            about_me.slice(4,about_me.length).map((value)=>(
                              <span>{value}.</span>
                            ))
                          }
                        </p>
                      </div>
                      
                       <a className="moreless-button" onClick={()=>{this.setState({hide:!this.state.hide})}}>
                        Read more
                      </a>
                      
                    </div>
                  </div>
                </div>
                {/* End intro */}
                {/* Passions */}
                <div className="right-mainContent">
                  <div className="myPassions">
                    <h2>My passions</h2>
                    <ul>
                      <li>
                        <i className="fa fa-gratipay" aria-hidden="true"></i>
                        <p>
                          {guider.passion}
                        </p>
                      </li>
                    </ul>
                  </div>
                  {/* End passions */}
                  {/* BookOffers */}
                  <div className="bookOffers">
                    <h2>Book one of my offers in Ha Noi</h2>
                    <ul>
                      {post}
                    </ul>
                  </div>
                  <div className="pagination">
                    <div className="paginationContent">{renderPageNumbers}</div>
                  </div>
                  {/* End bookOffers */}
                  <div className="requestForOffers">
                    <div className="requestForOffersContent">
                      <img src={guider.avatar} alt="logo" />
                      <h2>
                        Hi there!
                                <br />I can personalize your experience
                            </h2>
                      <p>
                        Just let me know your preferences for a private and personalize
                        experience!
                            </p>
                    </div>
                  </div>
                  {/* Review */}

                  {/* End Review */}
                  {/* End Load Review button */}
                  <div className="thisIsTravel">
                    <h2>This is Viet Nam</h2>
                    <video controls>
                      <source
                        src="/video/IMG_0601.TRIM.MOV"
                        type="video/mp4"
                      />
                    </video>
                  </div>

                </div>
              </div>
            </div>
            {/*  End Content  */}
          </div>
        </div>;

            </div>
    );
  }
}

export default ProfileGuiders;