import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import country from "../json/country.json";
import $ from "jquery";
import Config from "../Config";
import ReviewInPost from '../Guider/ReviewInPost';
import GuiderInPost from '../Guider/GuiderInPost';
import {connect} from 'react-redux';

class Pay extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tourDetail: {},
      country: [],
      postInfo: [],
      isDisabledPay: true,
      isDisabledCheckBox:true,
      link_youtube:'',
      errors:[],
      isError: false,
      data:{
        traveler_id:'',
        first_name: '',
        last_name: '',
        country_phone:'+84',
        phone:''
      }
    };
  }

  async componentDidMount() {
    this.setState({ country });
    //open loader to paypal
    

    $("#amout_show").hover(function(){
      $('.tool_tipPeople').show();
    },function(){
        $('.tool_tipPeople').hide();
    })

    $('.guiderNamePay').click(function () {
      if($('.guiderNamePay').hasClass('showing') === false){
        $('.tool_tipGuider').show();
        $('.guiderNamePay').addClass('showing');
      }else{
        $('.tool_tipGuider').hide();
        $('.guiderNamePay').removeClass('showing');
      }
    });

    $(document).mouseup(function (e) {
      var container = $(".tool_tipGuider");
      // if the target of the click isn't the container nor a descendant of the container
      if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.guiderNamePay').is(e.target)) {
        $('.tool_tipGuider').hide();
        $('.guiderNamePay').removeClass('showing');
      }

  });

    $('.titleTourPay').click(function () {
      if($('.titleTourPay').hasClass('showing') === false){
        $('.tool-tipPost').show();
        $('.titleTourPay').addClass('showing');
      }else{
        $('.tool-tipPost').hide();
        $('.titleTourPay').removeClass('showing');
      }
    });

    $(document).mouseup(function (e) {
          var container = $(".tool-tipPost");
          // if the target of the click isn't the container nor a descendant of the container
          if (!container.is(e.target) && container.has(e.target).length === 0 && !$('.titleTourPay').is(e.target)) {
            $('.tool-tipPost').hide();
            $('.titleTourPay').removeClass('showing');
          }
   
  });

    //load post data
    let tourDetail = JSON.parse(sessionStorage.getItem('tourDetail'));
    let response = await fetch(
      Config.api_url + "guiderpost/findSpecificPost?post_id=" + tourDetail.post_id,
      {
        method: "GET",
        mode: "cors",
        credentials: "include"
      }
    );
    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }

    const postInfo = await response.json();

    let link_youtube = postInfo.video_link;
      if(link_youtube.includes('youtu.be')){
        link_youtube = link_youtube.replace("youtu.be","youtube.com/embed");
        this.setState({link_youtube});
      }else{
        link_youtube = link_youtube.split("&");
        this.setState({link_youtube:link_youtube[0].replace("watch?v=","embed/")});
      }
      this.setState({ postInfo});
    
      //check infomation travel have exist
      const res = await fetch(
        Config.api_url + "Traveler/isLackingProfile?traveler_id="+this.props.user.id ,{
          method: "GET",
          mode: "cors",
          credentials: "include"
        });
        const result = await res.json();
        if(result === true){
          this.setState({isDisabledPay:true});
        }else{
          this.setState({isDisabledPay:false});
        }
  }

  async goToPayPal() {
    var data = JSON.parse(sessionStorage.getItem("tourDetail"));
    delete data.price;
    delete data.dateForBook;
    delete data.hourForBook;
    $(".coverLoader").show();
    let options = {
      method: "POST",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json"
      },
      body: JSON.stringify(data)
    };
    let response = await fetch(Config.api_url + "Payment/Pay", options);
    response = await response.text();
    window.location.href = response;
    sessionStorage.setItem("link", response);
  }



  validatePhone(phone) {
    const pattern = /^\d{10,11}$/;
    const result = pattern.test(phone);
    return result;
  }

  handleCheckBox = () =>{
    this.setState({isDisabledCheckBox:!this.state.isDisabledCheckBox});
  }

  handleChange(e) {
    const value = e.target.value;
    const name = e.target.name;
    let {errors} = this.state;
    const { data } = this.state;
    data[name] = value;
    if(value !== ''){
        errors[name] = '';
    }
    this.setState({ data });
  }

  isValidate = () => {
    const { data } = this.state;
    let isError = false;
    let errors = {};
    if(data.first_name === '') {
      isError = true;
      errors['first_name'] = 'First name is empty, Input your first name';
    }
    if(data.last_name === '') {
      isError = true;
      errors['last_name'] = 'Last name is empty, Input your last name';
    }
    if(this.validatePhone(data.phone) === false){
      isError = true;
      errors['phone'] = 'Phone must be digits and have 10-11 digits';
    }

    this.setState({ isError, errors });
    if(isError) 
      return true;

    return false;
  }

  submitForm = async e => {
    e.preventDefault();
    let {data} = this.state;  
    if(this.isValidate()) {
      return false;
    } 
    // let user = JSON.parse(window.sessionStorage.getItem('user'));
    data.traveler_id = this.props.user.id;
    try {
      const response = await fetch(Config.api_url + "Traveler/updateLackingProfile" ,{
        method: "POST",
        mode: "cors",
        credentials: "include",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
      // if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
      const guider = await response.json();
    } catch (err) {
          console.log(err);
    }
    this.setState({isDisabledPay:false});
  };

  render() {
    let country_name = this.state.country.map((value, index) => {
      return (
        <option key={index} value={value.name}>
          {value.name + "(" + value.code + ")"}
        </option>
      );
    });

    let country_phone = this.state.country.map((value, index) => {
      return (
        <option key={index} value={value.dial_code}>
          {value.name + "(" + value.dial_code + ")"}
        </option>
      );
    });
    let {postInfo} = this.state;
    console.log("postInfo",postInfo);
    let tourDetail = JSON.parse(sessionStorage.getItem("tourDetail"));
  
    // let user = JSON.parse(sessionStorage.getItem("user"));
    // if (tourDetail === null || user === null) {
    //   window.location.href = "/";
    //   sessionStorage.setItem("messagePay", "Error user or tour inf");
    // } else {
    //   sessionStorage.setItem("messagePay", "");
    // }
    let begin_date = tourDetail.begin_date.split(" ");
    let end_date = tourDetail.finish_date.split(" ");
    let {data,errors} = this.state;
    
    return (
      <div>
        <div className="coverLoader">
          <div className="loader"></div>
        </div>
        <div className="payForm">
          <div className="inputInfoPay">
            <div
              className={this.state.isDisabledPay ? "paypal_pay hidden" : ""}
            >
              <h2>Select payment</h2>
              <hr />
              <p className="guider_aggument">

This Agreement sets forth the terms and conditions between TravelWLocal and users who set up a tour guide profile and create tours and travel services offered for sale on the TravelWLocal website. This Agreement explains the relationship between You and TravelWLocal, how You set up your profile, create tours and travel services for sale and receive the money owed to You by TravelWLocal. This agreement specifically limits our liability.

You must read this Agreement before using the Site, creating a profile and setting up tours and travel services for sale on TravelWLocal. Use of the Site constitutes an agreement to all terms and conditions in this Agreement and You warrant that You understand, agree to and accept all terms and conditions contained here.
1.	General
TravelWLocal connects You with travelers who are seeking to obtain tours and travel related services. We do this through our website, TravelWLocal.com, which is a communications and payment processing system for enabling this connection. As well as maintaining the website, TravelWLocal handles customer service through telephone and email, markets the website through various means including search engine optimization, pay per click programs, public relations and advertising. Depending upon the transaction processing model You select, we may collect money on your behalf and remit it to You in a timely fashion.

You agree that the travelers who we connect with You are the customers of TravelWLocal and You will use your best efforts to ensure that any sales resulting from such customers are processed through the TravelWLocal website, or such other means as we mutually agree.

You acknowledge that communication between You and our customers may be monitored by TravelWLocal for quality control and training. We reserve the right to edit the messages You send to customers. If we do so, we will inform You of any changes made.

You represent and warrant to TravelWLocal that You are aware of and are in compliance with any licensing and legal restrictions that may be placed on the delivery by You of any tours or travel related services.

You further acknowledge and agree that You shall be solely responsible for the supply of any equipment required for a particular tour or travel services provided by You pursuant to the terms of this Agreement, and that You shall ensure, prior to the commencement of any such tour or travel services, that such equipment is in good working order and complies with all applicable safety standards.

This "Tour Guide Agreement" should be read in conjunction with the "Tour Guide Business Principles" and the "Customer Terms and Conditions", which applies to registered TravelWLocal customers.
2.	Acceptance as a Tour Guide
Any individual or company who is able to enter into a legally binding and enforceable contract may apply to become a TravelWLocal Tour Guide. Upon application through the Site, TravelWLocal will begin the acceptance process. Details of this process may include telephone interviews, submission of documents, checking of references and completion of training and examination.

TravelWLocal may make use of a third-party verification service in order to perform background checks. These services may verify information such as name, address, social security number, and criminal background.

The success of the TravelWLocal concept is based on the quality of the experience that You as a Tour Guide deliver. Therefore we are highly selective in the people we choose to work with.

We are committed to the success of our Tour Guides. Acceptance as a Tour Guide is at the sole discretion of TravelWLocal and we reserve the right to refuse anyone for any reason.

3.	Acceptance of "Tour Guide Business Principles"
You agree to abide by the TravelWLocal "Tour Guide Business Principles".

4.	Acceptance of "Customer Terms and Conditions"
You have read the TravelWLocal "Customer Terms and Conditions" and understand that these terms and conditions govern your relationship with our customers.

5.	Use of Site and Services
TravelWLocal connects You with travelers who are seeking tours and travel related services. We do this through our website, which is a communications and payment processing system for enabling this connection.

Upon our acceptance of You as a Tour Guide, You may use the Site to create a profile of yourself, set up web pages that describe the tours You provide and utilize the calendar to indicate your availability.

Once these are complete, TravelWLocal will publish them on the TravelWLocal website. We will then begin collecting bookings and appropriate payments on your behalf.

6.	Information Provided by You

You are responsible for providing accurate, timely and complete information to us in connection with your use of the Site. TravelWLocal is not responsible for any claims relating to any inaccurate, untimely or incomplete information provided to us by You. TravelWLocal will only use your information in accordance with our Privacy Policy.

TravelWLocal will use its best efforts to ensure the privacy of all other personal information, however we expressly disclaim any liability for any damage that may result should any information be released to any third parties. You hereby agree to hold TravelWLocal harmless for any damages that may result.

7.	Grant of Limited License
By posting content to the TravelWLocal Site, You:

a) grant TravelWLocal and its affiliates and licensees the right to use, reproduce, display, perform, adapt, modify, distribute, have distributed, and promote the content in any form, anywhere and for the purpose of selling and marketing TravelWLocal or your tour offerings; and

b) warrant and represent that You own or otherwise control all of the rights to the content and that use of your content by TravelWLocal does not violate the rights of any third party.

c) in the event that content you upload to your TravelWLocal website violates the rights of a third party, you agree to indemnify us against any losses and liabilities that may result from the action of such third party against TravelWLocal.

8.	Transactions
Travelers seek the services of Tour Guides through the use of the Site. Tour Guides may opt to have TravelWLocal collect the entire fee due for the services of the Tour Guide through our payments system.

Alternately, only in cases where it is impractical to remit money to You, Tour Guides may opt to have TravelWLocal collect only a booking fee. In this case, the traveler will make payment for the Tour Guide services purchased at the time the tour is taken, in the amount and currency specified at the time of booking. Whether You can use this alternative is at the sole discretion of TravelWLocal.

TravelWLocal will process refunds or credits for our errors or other extenuating circumstance.

9.	Booking Fee
In order to compensate TravelWLocal for its services, TravelWLocal charges a percentage of the cost of the tour. This percentage is set when your application is accepted. TravelWLocal may reduce this fee at any time, either permanently or for a limited period. It may be increased only upon 60 days notice to You.

10.	Refunds
Travelers may request a refund of the amount paid for a tour or tour service as set out in the " TravelWLocal Customer Terms and Conditions". In the event that a refund is made, only the amount actually received by TravelWLocal (if any), less our booking fee will be paid.

11.	Payments to You
If TravelWLocal collects the entire amount paid for the services You provide, we will pay You, for all funds actually received by us, less our booking fee, subject to any deductions for amounts withheld pursuant to section 10 above.

Any upfront costs specified by You on your tour description page will be paid on a weekly basis. The amount collected for your services, less our booking fee will be paid once the tour has been delivered, on a weekly basis.

In the rare case where TravelWLocal collects only the booking fee, then no further payment is due to You.

Payments are made according to a variety of methods, determined when your account is approved. At any time, You may request a change in payment method, from those currently available.

12.	Privacy
TravelWLocal is committed to ensuring the privacy of the information You give us.

13.	Limitation of Liability and Remedies

Under no circumstances will TravelWLocal be liable to You for any indirect, incidental, consequential, special or exemplary damages arising from any provision of this Agreement. Furthermore, the TOTAL liability of TravelWLocal arising with respect to this Agreement and the Site will not exceed the total amounts collected by us and not yet paid to You under this Agreement. Any notice or other communication to be given hereunder will be in writing and given by facsimile, postpaid registered or certified mail return receipt requested, or electronic mail.

Some jurisdictions do not allow the disclaimer of implied warranties. In such jurisdictions, the foregoing disclaimers may not apply to You insofar as they relate to implied warranties.

14.	Indemnification
Upon a request by TravelWLocal, You agree to defend, indemnify, and hold harmless TravelWLocal and other affiliated companies, and their employees, contractors, officers, and directors from all liabilities, claims, and expenses, including attorney's fees, that arise from your use or misuse of this Site. TravelWLocal reserves the right, at its own expense, to assume the exclusive defense and control of any matter otherwise subject to indemnification by You, in which event You will cooperate with TravelWLocal asserting any available defenses.

15.	Modification of the Terms of this Agreement

TravelWLocal reserves the right to make changes to this Agreement from time to time. TravelWLocal shall provide notice to You of any substantive and/or material changes to this Agreement or any policies posted on the Site. Such notice shall be by email and posting on the Site.

16.	Term of Agreement

This Agreement will become effective immediately your use of the Site and shall remain effective unless terminated by either party as provided hereunder. Either party may terminate this Agreement by providing the other with written or email notice of such termination which shall be effective immediately upon delivery of such notice to the other party. Furthermore, TravelWLocal may terminate this Agreement immediately for any breach of this Agreement, the "Tour Guides Rules of Conduct" or any applicable policy of TravelWLocal as posted on the Site from time to time. In the event of termination or expiration, the following sections shall survive: Term of Agreement; Ownership; Disclaimer; Limitation of Liability; Notice; and General Provisions.

Should You violate the terms of this Agreement, TravelWLocal reserves the right to terminate your use of this Site immediately at its sole discretion.

17.	Legal Claims

For all disputes between TravelWLocal and You relating to the Site, this Agreement, transactions facilitated or conducted through the Site, tours and travel services ordered or purchased through the Site, dealings between You and TravelWLocal, or any related matters, the parties will attempt to find the least onerous solution to the Dispute. If a Dispute cannot be resolved by the parties, then the Dispute must be resolved before a court of competent jurisdiction.

18.	General Provisions

Failure by TravelWLocal to enforce any provision(s) of this Agreement shall not be construed as a waiver of any provision or right. These Terms of Use, and all other aspects of use of the Site

These Terms of Use constitute the entire agreement between You and TravelWLocal with respect to the Site. If any provision of these Terms of Use is found to be invalid or unenforceable, the remaining provisions shall be enforced to the fullest extent possible, and the remaining Terms of Use shall remain in full force and effect. These Terms of Use inure to the benefit of TravelWLocal, its successors and assigns.

Nothing in this Agreement shall be construed as making either party the partner, joint venture, agent, legal representative, employer or employee of the other. Neither party shall have, or hold itself out to any third party as having, any authority to make any statements, representations or commitments of any kind, or to take any action, that shall be binding on the other, except as provided for herein or authorized in writing by the party to be bound.
</p>
                  
              <input type="checkbox" onClick={this.handleCheckBox}/> <span className="policy" >I agree with agguments</span>
              <div className="paypal_radio">
                {
                  this.state.isDisabledCheckBox ? '' 
                  : <input
                  type="radio"
                  name="paypal"
                  onChange={this.goToPayPal}
                /> 
                }{" "}
                <span>Paypal</span>
                <img src="/img/paypal.png" alt="paypal" />
              </div>
            </div>
            <div
              className={this.state.isDisabledPay ? "" : "paypal_pay hidden"}
            >

              <div className="infoTravellerPay">
                <p>First name</p>
                <input
                  type="text"
                  placeholder="Fisrt name"
                  name="first_name"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['first_name'] ? <p style={{color: "red"}} className="errorInput">{errors['first_name']}</p> : ''}

                <p>Last name</p>
                <input
                  type="text"
                  placeholder="Last name"
                  name="last_name"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                 {errors['last_name'] ? <p style={{color: "red"}} className="errorInput">{errors['last_name']}</p> : ''}

                <p>Phone</p>
                <select
                  style={{
                    width: "40%",
                    height: "42px",
                    border: "1px solid #eaeaea"
                  }}
                  name='country_phone'
                  value={data.country_phone}
                  onChange={e => {
                    this.handleChange(e);
                  }}
                >
                  {country_phone}
                </select>
                <input
                  type="text"
                  placeholder="Phone"
                  className="phone_traveller"
                  name="phone"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                 {errors['phone'] ? <p style={{color: "red"}} className="errorInput">{errors['phone']}</p> : ''}
                <input
                  type="submit"
                  value="Save"
                  className="saveInfoTraveller"
                  onClick={this.submitForm}
                />
              </div>
            </div>
          </div>

          {/* infoTourBook */}
          <div className="infoTourBook">
            <div className="intro_tour">
              <img className="payImg" alt="natural" src={`${postInfo.picture_link}`} />
              <h2 className="titleTourPay">{postInfo.title}</h2>
              <div className="tool-tipPost">
                  {/* Post */}
                  <div id="reactContainer">
                  {/*  Content  */}
                  <div className="content " id="contentPay">
                    <div className="content-right " id="content-rightPay">
                      <div className="PostDetail">
                        <div className="intro">
                        <iframe
                          src={this.state.link_youtube}
                          frameBorder="0"
                          allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                          allowFullScreen 
                        ></iframe>
                          <h2 className="titleTour">{postInfo.title}</h2>
                          <p className="introduceTour">{postInfo.description}</p>
                        </div>
                        <div className="activities">
                          <ul>
                            <li>
                              <i className="fa fa-map-marker"></i>
                              <span>{postInfo.location}</span>
                            </li>
                            <li>
                              <i className="fa fa-hourglass-end" />
                              <span>{postInfo.total_hour} hours</span>
                            </li>
                            <li>
                              <i className="fa fa-users" />
                              <span>Private tour. Only you and your host</span>
                            </li>
                          </ul>
                          <i className="fa fa-sticky-note" />
                          <span>
                            <strong>Including:</strong>
                            {postInfo.including_service}
                          </span>
                        </div>
                        
                      </div>
                    </div>
                  </div>
                </div>
                  {/* End Post */}
              </div>
            </div>
            <div className="info_tourDetail">
              <hr />
              <div className="tour_detailHour">
                <i className="fa fa-calendar-o celander" aria-hidden="true">
                  <span>Begin date: {begin_date[0]}</span>
                </i>
                <br/>
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>Begin time: {begin_date[1]}</span>
                </i>
                <br/>
                
                <i className="fa fa-calendar-o celander" aria-hidden="true">
                  <span>End date: {end_date[0]}</span>
                </i>
                <br/>
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>End time: {end_date[1]}</span>
                </i>
                <br/>
                <i className="fa fa-clock-o" aria-hidden="true">
                  <span>Tour duration: {postInfo.total_hour} hour</span>
                </i>
                <br/>
                <i className="fa fa-user" aria-hidden="true" style={{position:'relative'}}>
                  <span >
                    Guider: <span className="guiderNamePay">{tourDetail.guider_name}</span>
                  </span>
                  <div className="tool_tipGuider">
                    <div className="content-left">
                        <GuiderInPost
                          guiderId={tourDetail.guider_id}
                          postId={tourDetail.post_id}
                        />
                    </div>
                  </div>
                </i>
                <br/>
                <i className="fa fa-user" aria-hidden="true">
                  <span >
                    Amount people:
                    <span style={{color:"#e71575",position:'relative',cursor:'pointer'}} id="amout_show">
                    {" "}{parseInt(tourDetail.adult_quantity) +
                      parseInt(tourDetail.children_quantity)}
                      </span>
                      {" "}people
                  </span>
                  <div className="tool_tipPeople">
                    <i className="fa fa-user" aria-hidden="true">
                      <span>
                        Adult:{" "+tourDetail.adult_quantity}
                      </span>
                    </i>
                    <p></p>
                    <i className="fa fa-user" aria-hidden="true">
                      <span>
                      Children:{" "+ tourDetail.children_quantity}
                      </span>
                    </i>
                    <p></p>
                  </div>
                </i>
                <hr />
                <div className="total_priceBook">
                  Fee tour:
                  <span>{tourDetail.price}$</span>
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
	return {  user };
}
export default connect(mapStateToProps)(Pay);
