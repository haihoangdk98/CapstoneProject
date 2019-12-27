import React from 'react';
import Rated from './Rated';
import Config from '../Config';

class ReviewInPost extends React.Component {
      constructor(props) {
            super(props);
            let obj = [{
                  "review_id": "",
                  "post_id": "",
                  "guider_id": "",
                  "review": "",
                  "rated": 1,
                  "post_date": ""
            }];

            this.state = {
                  reviews: obj,
                  rating: 0,
                  isDisable: true,
                  page:0
            };

      }

      async componentDidMount() {
            let {page} = this.state;
            try {
                  const response = await fetch(Config.api_url + "review/reviewByPostId?post_id=" + this.props.postId+"&page="+page, {
                        method: "GET",
                        mode: "cors",
                        credentials: "include"
                  });
                  if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
                  const review = await response.json();
                  // console.log(review);
                  this.setState({ reviews: review });
            } catch (err) {
                  console.log(err);
            }
      }

      render() {
            var notification = this.state.isDisable ? "" : <p style={{ color: 'red' }}>Vote star before comment</p>;

            let showReview = <div>There is no review yet. Be the first to show up here</div>;
            if (this.state.reviews.length > 0) {
                  showReview = this.state.reviews.map((review, index) =>
                        <li key={index}>
                              <div className="review">
                                    <div className="reviewContainer">
                                          <img className="defaultLogo" src={review.traveler_image} alt="logo" />
                                          <div className="reviewInfo">
                                                <div className="nickName">{review.traveler_name}</div>
                                                <Rated number={review.rated} />
                                                <div className="dateComment">{review.post_date}</div>
                                          </div>
                                          <div className="commentDetails">{review.review}</div>
                                          <span className="reviewTitle">The Local!</span>
                                    </div>


                              </div>
                        </li>
                  );
            }
            return (
                  <ul className="listReview">
                        <h2>Reviews</h2>
                        {showReview}
                  </ul>
            );
      }
}


export default ReviewInPost;
