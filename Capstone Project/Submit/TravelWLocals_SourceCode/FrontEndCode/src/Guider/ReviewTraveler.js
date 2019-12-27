import React, { Component } from "react";
import Config from '../Config';
import { thisExpression } from "@babel/types";
import {connect} from 'react-redux';
class ReviewTraveler extends Component {
    constructor(props){
        super(props);
        this.state = {
            tvl:{
                language:['']
            },
            reviews:[],
            page:0,
            data:{},
            param:0,
            review:''
        }
    }

    componentWillMount(){
      // let user = this.props.user;
      //   if(user.role === 'TRAVELER'){
      //       sessionStorage.setItem('messagePay','You are Traveler');
      //       window.location.href = '/';
      //       return false;
      //     }
    }

    componentDidMount(){
      
        var param = this.props.match.params.id;
        let result = /^\+?(0|[1-9]\d*)$/.test(param);
        
        if(result){
            this.setState({param});
          this.loadData(param);
          this.loadReview(param,0);
        }else {
            window.location.href = '/page404'
        }
       
   }
 
    loadData = async (param) =>{
        const response = await fetch(
            Config.api_url + "Traveler/GetTraveler?traveler_id="+param,
            {
              method: "GET",
              mode: "cors",
              credentials: "include",
              headers: {
                Accept: "application/json"
              }
          });
      
          if (!response.ok) {
            window.location.href = '/page404';
            throw Error(response.status + ": " + response.statusText);
          }

          const tvl = await response.json();
          console.log(tvl);
          tvl.date_of_birth = tvl.date_of_birth.split(" ")[0];
          this.setState({tvl});
    }
    
    reviewFillter = (value) => {
      let {reviews} = this.state;
      value.post_date = value.post_date.split('T')[0];
      var count = 0;
      for(let i = 0;i < reviews.length;i++){
        if(reviews[i].review_id === value.review_id){
          count = 1;
        }
      }
      if(count === 0) reviews.push(value);
      console.log(reviews);
      this.setState({reviews});
    }

    loadReview = async (param,page) => {
      let {reviews} = this.state;
        const response = await fetch(
            Config.api_url + "review/showTravelerReview?traveler_id="+param+"&page="+page,
            {
              method: "GET",
              mode: "cors",
              credentials: "include"
          });
      
          if (!response.ok) {
            throw Error(response.status + ": " + response.statusText);
          }

          const tvl = await response.json();
         if(page === 0){
          tvl.map(value =>{
            value.post_date = value.post_date.split('T')[0];
            reviews.push(value);
          })
         this.setState({reviews});
         }else{
          tvl.map(value =>{
            this.reviewFillter(value);
          })
         }
    }

    loadMore = () =>{
        let {page,param} = this.state;
        page ++;
        this.loadReview(param,page);
        this.setState({page});
    }

     handleChange = (e) =>{
        let {data} = this.state;
        data[e.target.name] = e.target.value;
        this.setState({data});
     }

     handleSubmit = async () => {
        let {data,param,reviews} = this.state;
        let user = this.props.user;
        data.traveler_id = param;
        data.guider_id = user.id;
        let response = await fetch(Config.api_url+"review/createTravelerReview",
            {
                method: "POST",
                mode: "cors",
                credentials: "include",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }
        );
    if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
          
        let review = await response.json();
        data.review = '';
        review.post_date = review.post_date.split('T')[0];
        reviews.push(review);
        this.setState({data,reviews});
     }
    
    gender = (gender) =>{
        if(gender === 0) {
            return 'Male'
        }else if (gender === 1){
            return 'Female'
        }else{
            return 'Other';
        }
    }
  render() {
    let {tvl,reviews,data} = this.state;
    const gender = this.gender(tvl.gender);
    let showReview = reviews.map((value,index) => (
        <li key={index}>
        <div className="review">
          <div className="reviewContainer">
            <img
              className="defaultLogo"
              src={value.guider_image}
              alt="logo"
            />
            <div className="reviewInfo">
              <div className="nickName">{value.guider_name}</div>
              <div className="dateComment">{value.post_date}</div>
            </div>
            <div className="commentDetails">
              {value.review}
            </div>
            <span className="reviewTitle">{value.guider_name} The Guider!</span>
          </div>
        </div>
      </li>
    ))
    return (
      <div>
        <div>
          <div id="reactContainer">
            {/*  Content  */}
            <div className="content">
              <div className="content-left">
                <div className="profile-box">
                  <div className="pb-header header-stick">
                    <div className="header-pb">
                      <h1 className="TitlePb TileStickyPb">
                       {tvl.first_name +" "+ tvl.last_name}
                      </h1>
                    </div>
                    <div>
                      <img
                        className="pf-avatar"
                        src={tvl.avatar_link}
                      />
                    </div>
                  </div>
                  <p className="ListItem">
                    <span className="ListItemIcon">
                    <i className="fa fa-venus" aria-hidden="true"></i>
                    </span>
                    <span className="ListItemText">Gender {gender}</span>
                  </p>
                  <p className="ListItem">
                    <span className="ListItemIcon">
                     
                      <i className="fa fa-language" aria-hidden="true"></i>
                    </span>
                    <span className="ListItemText">I speak {tvl.language[0].toUpperCase()}</span>
                  </p>
                  <p className="ListItem">
                    <span className="ListItemIcon">
                        <i className="fa fa-birthday-cake" aria-hidden="true"></i>
                    </span>
                    <span className="ListItemText">Date of birth: {tvl.date_of_birth}</span>
                  </p>
                  <p className="ListItem">
                    <span className="ListItemIcon">
                    <i className="fa fa-globe"></i>
                    </span>
                    <span className="ListItemText">
                        Country: {tvl.country}
                    </span>
                  </p>
                </div>
              </div>
              <div className="content-right">
                <ul className="listReview" id="commentInTraveler">
                  <h2>Reviews</h2>
                 {showReview}
                <p style={{color:"#e71575",cursor:'pointer'}} onClick={this.loadMore}>Load more</p>
                  <textarea
                    id="subject"
                    name="review"
                    placeholder="Comment .."
                    style={{ height: "200px" }}
                    value={data.review}
                    onChange={this.handleChange}
                  />
                  <button onClick={this.handleSubmit} id="commentPFT" >Commnent</button>
                </ul>
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
      return {user};
}
export default connect(mapStateToProps)(ReviewTraveler);
