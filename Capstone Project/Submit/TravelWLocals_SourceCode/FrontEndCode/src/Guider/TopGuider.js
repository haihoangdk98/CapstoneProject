import React, { Component } from "react";
import Config from "../Config";
import SweetAlert from "react-bootstrap-sweetalert";
import { Link } from "react-router-dom";
import Rated from "./Rated";
import $ from "jquery";

class TopGuider extends Component {
      constructor(props) {
            super(props);
            this.state = {
                  postsRate: [],
                  postsContribute: [],
                  
            };
      }

      async componentDidMount() {

            try {
                  const responseRate = await fetch(
                        Config.api_url + "Guider/getTopGuiderByRate",
                        {
                              method: "GET",
                              mode: "cors",
                              credentials: "include"
                        }
                  );
                  if (!responseRate.ok) {
                        throw Error(responseRate.status + ": " + responseRate.statusText);
                  }

                  const responseContribute = await fetch(
                        Config.api_url + "Guider/getTopGuiderByContribute",
                        {
                              method: "GET",
                              mode: "cors",
                              credentials: "include"
                        }
                  );
                  if (!responseContribute.ok) {
                        throw Error(
                              responseContribute.status + ": " + responseContribute.statusText
                        );
                  }

                  const posts = await responseRate.json();
                  const postsContribute = await responseContribute.json();
                  this.setState({ postsRate: posts, postsContribute:postsContribute});
            } catch (err) {
                  console.log(err);
            }
            

            // window.onscroll = function () {
            //       if (window.pageYOffset === 0) {
            //             $("#navbar").css({ background: "none", "border-bottom": "none" });
            //             $(".navbarRightContent ul li").css({
            //                   color: "black",
            //                   "font-size": "18px"
            //             });
            //       }
            // };
      }


      render() {

            let guiderByRate = this.state.postsRate.map((post, index) => {
                  let bgImg = post.avatar;
                  return (
                        <div className="profile-box" key={index}>
                              <div className="pb-header header-stick">
                                    <div className="header-pb">
                                          <h1 className="TitlePb TileStickyPb">
                                                {post.first_name + "" + post.last_name}
                                          </h1>
                                    </div>
                              </div>
                              <img src={bgImg} className="imgpb-header"/>
                              <Rated number={post.rated} />
                              <Link to={"/guider/" + post.guider_id}>
                                    <button className="contactMe">About me</button>
                              </Link>
                        </div>
                  );
            });

            let guiderByContribute = this.state.postsContribute.map((post, index) => {
                  let bgImg = post.avatar;
                  return (
                        <div className="profile-box" key={index}>
                              <div className="pb-header header-stick">
                                    <div className="header-pb">
                                          <h1 className="TitlePb TileStickyPb">
                                                {post.first_name + "" + post.last_name}
                                          </h1>
                                    </div>
                              </div>
                              <img src={bgImg} className="imgpb-header"/>
                              <Rated number={post.rated} />
                              <Link to={"/guider/" + post.guider_id}>
                                    <button className="contactMe">About me</button>
                              </Link>
                        </div>
                  )
            });
            
            return (
                  <div className="topGuider">
                        <div className="topGuiderByRate">
                              <h1>Top guider by Rate</h1>
                              <div className="content-left">{guiderByRate}</div>
                        </div>
                        <div className="topGuiderByRate">
                              <h1>Top guider by Contribute</h1>
                              <div className="content-left">{guiderByContribute}</div>
                        </div>
                  </div>

            );
      }
}

export default TopGuider;
