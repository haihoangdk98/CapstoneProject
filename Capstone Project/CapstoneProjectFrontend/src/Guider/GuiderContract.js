import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import country from "../json/country.json";
import Config from '../Config';
import SweetAlert from 'react-bootstrap-sweetalert';
import {connect} from 'react-redux';
class GuiderContract extends Component {
  constructor(props) {
    super(props);

    this.state = {
      tourDetail: {},
      country: [],
      errors:[],
      isError: false,
      isClickCheckBox:false,
      data:{
        guider_id:'',
        first_name: '',
        name:'',
        last_name: '',
        identity_card_number: '',
        card_issued_date: '',
        gender: '0',
        hometown:'',
        date_of_birth:'1970-01-01',
        address:'',
        nationality: 'Vietnam',
        day:'01',
        month:'01',
        year:'1970',
        dayCard:'01',
        monthCard:'01',
        yearCard:'1970',
        card_issued_province:'',
        file_link:''
      }
      
    };
  }

  async componentDidMount() {
    this.setState({ country });
    let messages = this.props.message;
     if(messages === 'Waiting'){
       this.statusProfile("If you have sent us a contract, please wait while we processing it.If you haven't sent one then please fills in the form and sends it to us.",'Notification');
     }
  }


  onFileChangeHandler = (e) => {
    e.preventDefault();
    const { data,errors } = this.state;
    if(e.target.files[0] === undefined){
      data['file_link'] = '';
    }else{
      data['file_link'] = e.target.files[0].name;
    errors['file_link'] = '';
    this.setState({
        selectedFile: e.target.files[0],
        data,errors
    });
    }

};

  handleCheckBox = () =>{
    this.setState({isClickCheckBox:!this.state.isClickCheckBox});
    let {errors} = this.state;
    if(this.state.isClickCheckBox === false){
      errors['aggument'] = '';
    }
    this.setState({errors});
  }

  genderOnChange = e => {
    let {data} = this.state;
    data[e.target.name] = e.target.value;
    this.setState({data});
  };

  handleChange = (e)=>{
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
    const { data ,isClickCheckBox } = this.state;
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

    var letters = /^[0-9]+$/;
    if(!data.identity_card_number.match(letters)){
      isError = true;
      errors['identity_card_number'] = 'Identity card number must be digits';
    }
    if(data.hometown === ''){
      isError = true;
      errors['hometown'] = 'Home town is empty, Input your home town';
    }
    if(data.address === ''){
      isError = true;
      errors['address'] = 'Address is empty, Input your address';
    }
    if(data.card_issued_province === ''){
      isError = true;
      errors['card_issued_province'] = 'Card issued province is empty, Input your card issued province';
    }if(isClickCheckBox === false) {
      isError = true;
      errors['aggument'] = 'Agree with aggument';
    }if(!(data.file_link.toLowerCase()).includes('.pdf')) {
      isError = true;
      errors['file_link'] = 'File import must be PDF';
    }



    this.setState({ isError, errors });
    if(isError) 
      return true;

    return false;
  }

  hideAlert(type) {
    this.setState({
      alert: null
    });
    
    if(type !== "Notification"){
      window.location.href = '/contract';
    }
  }

  statusProfile(message,type){
    const getAlert = () => (
      <SweetAlert 
        success 
        onConfirm={() => this.hideAlert(type)}
      >
        {message}
      </SweetAlert>
    );

    this.setState({
      alert: getAlert()
    });
  }

  async submitForm (e){
    e.preventDefault();
    if(this.isValidate()) {
      return false;
    } 
    // data
    let user = this.props.user;
    let {data} = this.state;
    data.guider_id = user.id;
    data.date_of_birth = data.month+'/'+data.day+'/'+data.year+" 00:00";
    data.card_issued_date = data.monthCard+'/'+data.dayCard+'/'+data.yearCard+" 00:00";
    data.name = data.first_name +' '+ data.last_name;
    // end data

    try {
      const response = await fetch(Config.api_url + "Guider/CreateContract" ,{
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
      const formData = new FormData();
      formData.append('file', this.state.selectedFile);    
      formData.append('contract_id', guider);    
      fetch(Config.api_url+'Guider/UploadDocument', {
          method: 'post',
          mode: "cors",
          credentials: "include",
          body: formData
      }).then(res => {
          if(res.ok) {
        
          }
      });
      this.statusProfile('Create contract success','Success');
} catch (err) {
      console.log(err);
}
  }


  render() {
    

    const { errors,data} = this.state;
    let country_name = this.state.country.map((value, index) => {
      return (
        <option key={index} value={value.name}>
          {value.name + "(" + value.code + ")"}
        </option>
      );
    });
    var day = [];
    for (var i = 1; i <= 31; i++){
        day.push(i);
    } 
    var dayOption = day.map((value,index) => {
      if(parseInt(value) < 10) {
      return <option value={"0"+ value} key={index}>{value}</option>
      }
      return <option value={value} key={index}>{value}</option>
    }
    )

    var month = ["January","February","March","April","May","June","July","August","September","October","November","December"];
    var monthOptiom = month.map((value,index)=>{
      if(parseInt(index + 1) < 10){
        return (<option value={"0" + (index + 1)} key={index}>{value}</option>)
      }

      return <option value={index + 1} key={index}>{value}</option>
    });
    
    var currentYear = parseInt(new Date().getFullYear());
    var year = [];
    for (var i = currentYear; i >= 1950 ; i--){
      year.push(i);
    } 
    var yearOption = year.map((value,index) => (
      <option value={value} key={index}>{value}</option>
    )
    )

    return (
      <div>
          {this.state.alert}
        <div className="payForm contract">
          <div className="inputInfoPay contractContent">
          <h1>Contract</h1>
              <div className="gender">
                <p>Gender</p>
                <input
                  type="radio"
                  className="gendermale"
                  name="gender"
                  value= '1'
                  checked={this.state.data.gender === '1'}
                  onChange={e => this.genderOnChange(e)}
                />{" "}
                Male
                <input
                  type="radio"
                  name="gender"
                  value='2'
                  checked={this.state.data.gender === '2'}
                  onChange={e => this.genderOnChange(e)}
                />{" "}
                Female
                <input
                  type="radio"
                  name="gender"
                  value='0'
                  checked={this.state.data.gender === '0'}
                  onChange={e => this.genderOnChange(e)}
                />{" "}
                Other
              </div>
              {errors['gender'] ? <p style={{color: "red"}} className="errorInput">{errors['gender']}</p> : ''}

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
                

                <div className="label-information">Date Of Birth</div>
                    <div style={{ marginBottom: '20px' }}>
                    <select className="DOB" value={data.day} name="day" onChange={(e)=>{this.handleChange(e)}}>
                    {dayOption}
                    </select>
                    <select className="DOB" value={data.month} name="month" onChange={(e)=>{this.handleChange(e)}}>
                    {monthOptiom}
                    </select>
                    <select className="DOB" value={data.year} name="year" onChange={(e)=>{this.handleChange(e)}}>
                    {yearOption}
                    </select>
                </div>

                <p>Home town</p>
                <input
                  type="text"
                  placeholder="Home town"
                  name="hometown"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
              {errors['hometown'] ? <p style={{color: "red"}} className="errorInput">{errors['hometown']}</p> : ''}

                <p>Address</p>
                <input
                  type="text"
                  placeholder="Address"
                  name="address"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {this.state.hometownError ? (
                  <p style={{ color: "red" }} className="errorInput">
                    Please enter your hometown
                  </p>
                ) : (
                  ""
                )}
                {errors['address'] ? <p style={{color: "red"}} className="errorInput">{errors['address']}</p> : ''}

                <p>Country of residence</p>
                <select
                  style={{
                    width: "100%",
                    height: "40px",
                    border: "1px solid #eaeaea"
                  }}
                  name="nationality"
                  value={data.nationality}
                  onChange={(e)=>{this.handleChange(e)}}
                >
                  {country_name}
                </select>

                <p>Identity card number</p>
                <input
                  type="text"
                  placeholder="Identity card number"
                  className="phone_traveller"
                  name="identity_card_number"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['identity_card_number'] ? <p style={{color: "red"}} className="errorInput">{errors['identity_card_number']}</p> : ''}


                <div className="label-information">Card issued date</div>
                    <div style={{ marginBottom: '20px' }}>
                    <select className="DOB" value={data.dayCard} name="dayCard" onChange={(e)=>{this.handleChange(e)}}>
                    {dayOption}
                    </select>
                    <select className="DOB" value={data.monthCard} name="monthCard" onChange={(e)=>{this.handleChange(e)}}>
                    {monthOptiom}
                    </select>
                    <select className="DOB" value={data.yearCard} name="yearCard" onChange={(e)=>{this.handleChange(e)}}>
                    {yearOption}
                    </select>
                </div>

                <p>Card issued province</p>
                <input
                  type="text"
                  placeholder="Card issued province"
                  className="phone_traveller"
                  name="card_issued_province"
                  onChange={e => {
                    this.handleChange(e);
                  }}
                />
                {errors['card_issued_province'] ? <p style={{color: "red"}} className="errorInput">{errors['card_issued_province']}</p> : ''}

                <p>Upload file PDF</p>
                <input
                  type="file"
                  className="phone_traveller"
                  name="file_link"
                  onChange={this.onFileChangeHandler}
                />
                {errors['file_link'] ? <p style={{color: "red"}} className="errorInput">{errors['file_link']}</p> : ''}

                <div>
                <h2 style={{marginTop:"20px"}}>Guider Aggument</h2>
                  <p className="guider_aggument">

This Agreement sets forth the terms and conditions between TravelWLocal and users who set up a tour guide profile and create tours and travel services offered for sale on the TravelWLocal website. This Agreement explains the relationship between You and TravelWLocal, how You set up your profile, create tours and travel services for sale and receive the money owed to You by TravelWLocal. This agreement specifically limits our liability.<br/>

You must read this Agreement before using the Site, creating a profile and setting up tours and travel services for sale on TravelWLocal. Use of the Site constitutes an agreement to all terms and conditions in this Agreement and You warrant that You understand, agree to and accept all terms and conditions contained here.<br/>
1.	General<br/>
TravelWLocal connects You with travelers who are seeking to obtain tours and travel related services. We do this through our website, TravelWLocal.com, which is a communications and payment processing system for enabling this connection. As well as maintaining the website, TravelWLocal handles customer service through telephone and email, markets the website through various means including search engine optimization, pay per click programs, public relations and advertising. Depending upon the transaction processing model You select, we may collect money on your behalf and remit it to You in a timely fashion.

You agree that the travelers who we connect with You are the customers of TravelWLocal and You will use your best efforts to ensure that any sales resulting from such customers are processed through the TravelWLocal website, or such other means as we mutually agree.

You acknowledge that communication between You and our customers may be monitored by TravelWLocal for quality control and training. We reserve the right to edit the messages You send to customers. If we do so, we will inform You of any changes made.

You represent and warrant to TravelWLocal that You are aware of and are in compliance with any licensing and legal restrictions that may be placed on the delivery by You of any tours or travel related services.

You further acknowledge and agree that You shall be solely responsible for the supply of any equipment required for a particular tour or travel services provided by You pursuant to the terms of this Agreement, and that You shall ensure, prior to the commencement of any such tour or travel services, that such equipment is in good working order and complies with all applicable safety standards.

This "Tour Guide Agreement" should be read in conjunction with the "Tour Guide Business Principles" and the "Customer Terms and Conditions", which applies to registered TravelWLocal customers.<br/>
2.	Acceptance as a Tour Guide<br/>
Any individual or company who is able to enter into a legally binding and enforceable contract may apply to become a TravelWLocal Tour Guide. Upon application through the Site, TravelWLocal will begin the acceptance process. Details of this process may include telephone interviews, submission of documents, checking of references and completion of training and examination.

TravelWLocal may make use of a third-party verification service in order to perform background checks. These services may verify information such as name, address, social security number, and criminal background.

The success of the TravelWLocal concept is based on the quality of the experience that You as a Tour Guide deliver. Therefore we are highly selective in the people we choose to work with.

We are committed to the success of our Tour Guides. Acceptance as a Tour Guide is at the sole discretion of TravelWLocal and we reserve the right to refuse anyone for any reason.<br/>

3.	Acceptance of "Tour Guide Business Principles"<br/>
You agree to abide by the TravelWLocal "Tour Guide Business Principles".<br/>

4.	Acceptance of "Customer Terms and Conditions"<br/>
You have read the TravelWLocal "Customer Terms and Conditions" and understand that these terms and conditions govern your relationship with our customers.<br/>

5.	Use of Site and Services<br/>
TravelWLocal connects You with travelers who are seeking tours and travel related services. We do this through our website, which is a communications and payment processing system for enabling this connection.

Upon our acceptance of You as a Tour Guide, You may use the Site to create a profile of yourself, set up web pages that describe the tours You provide and utilize the calendar to indicate your availability.

Once these are complete, TravelWLocal will publish them on the TravelWLocal website. We will then begin collecting bookings and appropriate payments on your behalf.<br/>

6.	Information Provided by You<br/>

You are responsible for providing accurate, timely and complete information to us in connection with your use of the Site. TravelWLocal is not responsible for any claims relating to any inaccurate, untimely or incomplete information provided to us by You. TravelWLocal will only use your information in accordance with our Privacy Policy.

TravelWLocal will use its best efforts to ensure the privacy of all other personal information, however we expressly disclaim any liability for any damage that may result should any information be released to any third parties. You hereby agree to hold TravelWLocal harmless for any damages that may result.<br/>

7.	Grant of Limited License<br/>
By posting content to the TravelWLocal Site, You:<br/>

a) grant TravelWLocal and its affiliates and licensees the right to use, reproduce, display, perform, adapt, modify, distribute, have distributed, and promote the content in any form, anywhere and for the purpose of selling and marketing TravelWLocal or your tour offerings; and<br/>

b) warrant and represent that You own or otherwise control all of the rights to the content and that use of your content by TravelWLocal does not violate the rights of any third party.<br/>

c) in the event that content you upload to your TravelWLocal website violates the rights of a third party, you agree to indemnify us against any losses and liabilities that may result from the action of such third party against TravelWLocal.<br/>

8.	Transactions<br/>
Travelers seek the services of Tour Guides through the use of the Site. Tour Guides may opt to have TravelWLocal collect the entire fee due for the services of the Tour Guide through our payments system.

Alternately, only in cases where it is impractical to remit money to You, Tour Guides may opt to have TravelWLocal collect only a booking fee. In this case, the traveler will make payment for the Tour Guide services purchased at the time the tour is taken, in the amount and currency specified at the time of booking. Whether You can use this alternative is at the sole discretion of TravelWLocal.

TravelWLocal will process refunds or credits for our errors or other extenuating circumstance.<br/>

9.	Booking Fee<br/>
In order to compensate TravelWLocal for its services, TravelWLocal charges a percentage of the cost of the tour. This percentage is set when your application is accepted. TravelWLocal may reduce this fee at any time, either permanently or for a limited period. It may be increased only upon 60 days notice to You.<br/>

10.	Refunds<br/>
Travelers may request a refund of the amount paid for a tour or tour service as set out in the " TravelWLocal Customer Terms and Conditions". In the event that a refund is made, only the amount actually received by TravelWLocal (if any), less our booking fee will be paid.<br/>

11.	Payments to You<br/>
If TravelWLocal collects the entire amount paid for the services You provide, we will pay You, for all funds actually received by us, less our booking fee, subject to any deductions for amounts withheld pursuant to section 10 above.

Any upfront costs specified by You on your tour description page will be paid on a weekly basis. The amount collected for your services, less our booking fee will be paid once the tour has been delivered, on a weekly basis.

In the rare case where TravelWLocal collects only the booking fee, then no further payment is due to You.

Payments are made according to a variety of methods, determined when your account is approved. At any time, You may request a change in payment method, from those currently available.<br/>

12.	Privacy<br/>
TravelWLocal is committed to ensuring the privacy of the information You give us.<br/>

13.	Limitation of Liability and Remedies<br/>

Under no circumstances will TravelWLocal be liable to You for any indirect, incidental, consequential, special or exemplary damages arising from any provision of this Agreement. Furthermore, the TOTAL liability of TravelWLocal arising with respect to this Agreement and the Site will not exceed the total amounts collected by us and not yet paid to You under this Agreement. Any notice or other communication to be given hereunder will be in writing and given by facsimile, postpaid registered or certified mail return receipt requested, or electronic mail.

Some jurisdictions do not allow the disclaimer of implied warranties. In such jurisdictions, the foregoing disclaimers may not apply to You insofar as they relate to implied warranties.<br/>

14.	Indemnification<br/>
Upon a request by TravelWLocal, You agree to defend, indemnify, and hold harmless TravelWLocal and other affiliated companies, and their employees, contractors, officers, and directors from all liabilities, claims, and expenses, including attorney's fees, that arise from your use or misuse of this Site. TravelWLocal reserves the right, at its own expense, to assume the exclusive defense and control of any matter otherwise subject to indemnification by You, in which event You will cooperate with TravelWLocal asserting any available defenses.<br/>

15.	Modification of the Terms of this Agreement<br/>

TravelWLocal reserves the right to make changes to this Agreement from time to time. TravelWLocal shall provide notice to You of any substantive and/or material changes to this Agreement or any policies posted on the Site. Such notice shall be by email and posting on the Site.<br/>

16.	Term of Agreement<br/>

This Agreement will become effective immediately your use of the Site and shall remain effective unless terminated by either party as provided hereunder. Either party may terminate this Agreement by providing the other with written or email notice of such termination which shall be effective immediately upon delivery of such notice to the other party. Furthermore, TravelWLocal may terminate this Agreement immediately for any breach of this Agreement, the "Tour Guides Rules of Conduct" or any applicable policy of TravelWLocal as posted on the Site from time to time. In the event of termination or expiration, the following sections shall survive: Term of Agreement; Ownership; Disclaimer; Limitation of Liability; Notice; and General Provisions.

Should You violate the terms of this Agreement, TravelWLocal reserves the right to terminate your use of this Site immediately at its sole discretion.<br/>

17.	Legal Claims<br/>

For all disputes between TravelWLocal and You relating to the Site, this Agreement, transactions facilitated or conducted through the Site, tours and travel services ordered or purchased through the Site, dealings between You and TravelWLocal, or any related matters, the parties will attempt to find the least onerous solution to the Dispute. If a Dispute cannot be resolved by the parties, then the Dispute must be resolved before a court of competent jurisdiction.<br/>

18.	General Provisions<br/>

Failure by TravelWLocal to enforce any provision(s) of this Agreement shall not be construed as a waiver of any provision or right. These Terms of Use, and all other aspects of use of the Site

These Terms of Use constitute the entire agreement between You and TravelWLocal with respect to the Site. If any provision of these Terms of Use is found to be invalid or unenforceable, the remaining provisions shall be enforced to the fullest extent possible, and the remaining Terms of Use shall remain in full force and effect. These Terms of Use inure to the benefit of TravelWLocal, its successors and assigns.

Nothing in this Agreement shall be construed as making either party the partner, joint venture, agent, legal representative, employer or employee of the other. Neither party shall have, or hold itself out to any third party as having, any authority to make any statements, representations or commitments of any kind, or to take any action, that shall be binding on the other, except as provided for herein or authorized in writing by the party to be bound.
</p>
                  
                  <input type="checkbox" onClick={this.handleCheckBox}/> <span className="policy" >I agree with agguments</span>
                  {errors['aggument'] ? <p style={{color: "red"}} className="errorInput">{errors['aggument']}</p> : ''}
                </div>
                <input
                  type="submit"
                  value="Save"
                  className="saveInfoTraveller"
                  onClick={(e) => {this.submitForm(e)}}
                />
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
export default connect(mapStateToProps)(GuiderContract);
