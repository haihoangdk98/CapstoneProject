import React from 'react';
import Config from '../Config';
import ReactDOM from 'react-dom';


class PlanInPost extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      plans: {
        meeting_point: "",
        detail: ""
      }
    };
  }
  
  async componentDidMount() {
    try {
      const response = await fetch(
        Config.api_url + "plan/" + this.props.postId,
        {
          method: "GET",
          mode: "cors",
          credentials: "include",
          headers: {
            Accept: "application/json"
          }
        }
      );
      if (!response.ok) {
        throw Error(response.status + ": " + response.statusText);
      }

      const plan = await response.json();
      
      this.setState({ plans: plan });
    } catch (err) {
      console.log(err);
    }
  }
  checkPath = () =>{
    let pathname = window.location.pathname;
    if(pathname.includes('/chatbox')){
      return 'planInfo planWidthChatBox';
    }else{
      return 'planInfo';
    }
    
  }

  render() {
    let { plans } = this.state;
    let planInPost = this.checkPath();
    return (
      <div id="planInPost" className={planInPost} >
        <h2>This is our plan</h2>
        <p>
          Check out the plan below to see what you'll get up to with your local
          host.
        </p>
        <p> Feel free to personalize this offer.</p>
        <div className="meetPoint">
          <i className="fa fa-map-marker" aria-hidden="true"></i>
          <div className="detailPlan">
            <h4>Meeting point</h4>
            <p>{plans.meeting_point}</p>
          </div>
            <div dangerouslySetInnerHTML={{__html: plans.detail}} />
        </div>
      </div>
    );
  }
}

export default PlanInPost;
