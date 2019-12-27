import React, { Component } from 'react';
import "font-awesome/css/font-awesome.min.css";
import $ from 'jquery';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

class Tour extends Component {
  constructor(props){
    super(props);
    this.state = {
      numberInjoy: {
        adult: 1,
        children: 0
      },
      tourDate: new Date(),
      slideShow: [
        '/img/natural1.jpg',
        '/img/nature2.jpg',
        '/img/nature3.jpg',
        '/img/nature4.jpg',
        '/img/nature5.jpg',
        '/img/nature6.jpg',
      ],
      currentIndex: 0,
      intervalId: null
    }
  }
    componentDidMount() {
        
        //read more and read less
        $('.moreless-button').click(function () {
            $('.moretext').slideToggle();
            if ($('.moreless-button').text() === "Read more") {
                $(this).text("Read less")
            } else {
                $(this).text("Read more")
            }
        });
        //them bot nguoi du lich
        $(document).mouseup(function(e) 
            {
                if(e.button == 0){
                var container = $(".viewNumberTravel");
                var button = $("#Button-1bHL6");
                if (!container.is(e.target) && container.has(e.target).length === 0 && !button.is(e.target) && button.has(e.target).length === 0 ) {
                    $(".viewNumberTravel").hide();
                }
                }
            });

            $(document).on('click', '.DropdownButton-15Fja', function() {
                if($(".viewNumberTravel").css('display') === 'none') {
                $(".viewNumberTravel").show();
                } else {
                $(".viewNumberTravel").hide();
                }
            });

            this.setupInterval();
    }

    timer = () => {
      // setState method is used to update the state
      this.commonNext();
   }

    setupInterval = () => {
      var intervalId = setInterval(this.timer, 3000);
      this.setState({ intervalId: intervalId });
    }

    changeNumber = (age, minusPlus) => {
      const { numberInjoy } = this.state;
      const min = 1;
      const max = 8;
      var currentPeople = numberInjoy.adult + numberInjoy.children;
      if (age === "adult" && minusPlus === "minus") {
       if(currentPeople > min && numberInjoy.adult > 1){
        numberInjoy.adult--;
        this.setState({ numberInjoy: numberInjoy });
       }
      } else if (age === "adult" && minusPlus === "plus") {
        if(currentPeople < max){
          numberInjoy.adult++;
          this.setState({ numberInjoy: numberInjoy });
        }
      } else if (age === "child" && minusPlus === "minus") {
        if(currentPeople > min && numberInjoy.children > 0){
          numberInjoy.children--;
          this.setState({ numberInjoy: numberInjoy });
        }
      } else if (age === "child" && minusPlus === "plus") {
        if(currentPeople < max){
          numberInjoy.children++;
          this.setState({ numberInjoy: numberInjoy });
        }
      }
    };

    dateChange = date => {
      this.setState({
        tourDate: date
      });
    };
  
    commonNext  = () => {
      let { currentIndex, slideShow } = this.state;
      currentIndex++;
      if(currentIndex >= slideShow.length) {
        currentIndex = 0;
      }
      this.setState({ currentIndex });
    }


    handleNext = () => {
      clearInterval(this.state.intervalId);
      this.commonNext();
      this.setupInterval();
    }

    handlePrev = () => {
      clearInterval(this.state.intervalId);
      let { currentIndex, slideShow } = this.state;
      currentIndex--;
      if(currentIndex < 0) {
        currentIndex = slideShow.length - 1;
      }
      this.setState({ currentIndex });
      this.setupInterval();
    }

    handleChoose = (index) => {
      clearInterval(this.state.intervalId);
      this.setState({ currentIndex: index });
      this.setupInterval();
    }

    render() {
      const {numberInjoy } = this.state;
        return (
            <div>
            
                <div id="reactContainer">
  {/*  Content  */}
  <div className="content">
    <div className="content-left">
      <div className="book_block">
        <div className="experience_price">$45.00 per person</div>
        <div className="Rating">
          <i className="fa fa-star" />
          <i className="fa fa-star" />
          <i className="fa fa-star" />
          <i className="fa fa-star" />
          <i className="fa fa-star" />
          250 reviews
        </div>
        <div>
          <div className="DateSelector-2OYxm">
          <i className="fa fa-calendar-o" aria-hidden="true"></i>
            <DatePicker
                  selected={this.state.tourDate}
                  onChange={this.dateChange}
                ></DatePicker>
          </div>
          <div className="numberTravel">
            <span>
              <span className="PeopleSelector-3cPTh">
                <button
                  className="Button-1bHL5 DropdownButton-15Fja"
                  id="Button-1bHL6"
                >
                  <span className="ProfileIconContainer-3Xala">
                    <svg
                      className="ProfileIcon-3FvCp"
                      width={16}
                      height={16}
                      viewBox="0 0 20 23"
                    >
                      <g>
                        <path d="M15.414094,6.65663665 C15.414094,3.52796224 12.9882716,1 10.0062103,1 C7.02509025,1 4.59942235,3.52812516 4.59942235,6.65663665 C4.59942235,9.78514815 7.02509025,12.3132733 10.0062103,12.3132733 C12.9882716,12.3132733 15.414094,9.78531106 15.414094,6.65663665 Z M16.4126481,6.65663665 C16.4126481,10.3285794 13.5485479,13.3132733 10.0047644,13.3132733 C6.46189225,13.3132733 3.59797646,10.3283892 3.59797646,6.65663665 C3.59797646,2.9848841 6.46189225,0 10.0047644,0 C13.5485479,0 16.4126481,2.98469387 16.4126481,6.65663665 Z"></path>
                        <path d="M1.01540138,21.9992828 C1.19144555,18.0419836 3.44634003,14.5899302 6.73724542,13.1335217 C6.98976422,13.0217681 7.10387696,12.726467 6.99212337,12.4739482 C6.88036977,12.2214294 6.58506864,12.1073166 6.33254984,12.2190702 C2.53516372,13.8996246 0.00429829063,17.9459179 0.00429829063,22.4992828 L0.00429829063,22.9992828 L20.003133,22.9992828 L20.003133,22.4992828 C20.003133,17.9435721 17.4710994,13.897152 13.6713661,12.2178274 C13.4187915,12.1062 13.1235474,12.2204602 13.0119199,12.4730347 C12.9002924,12.7256093 13.0145526,13.0208534 13.2671272,13.1324809 C16.5600972,14.5878371 18.8160013,18.0399474 18.9920364,21.9992828 L1.01540138,21.9992828 Z"></path>
                      </g>
                    </svg>
                  </span>
                  <span className="ButtonText-3rr6g">{numberInjoy.adult} Adults and {numberInjoy.children}{" "}
                        children</span>
                  <svg
                    className="ChevronIcon-1tmys"
                    width={9}
                    height={5}
                    viewBox="0 0 15 9"
                  >
                    <path d="M7.5,5.66602416 L2.43674491,0.431396522 C1.9082727,-0.114962532 1.01849523,-0.14659907 0.449371305,0.360734337 C-0.119752618,0.868067744 -0.15270734,1.72225426 0.375764875,2.26861331 L6.46950998,8.56860926 C7.02586699,9.14379691 7.97413301,9.14379691 8.53049002,8.56860926 L14.6242351,2.26861331 C15.1527073,1.72225426 15.1197526,0.868067744 14.5506287,0.360734337 C13.9815048,-0.14659907 13.0917273,-0.114962532 12.5632551,0.431396522 L7.5,5.66602416 Z"></path>
                  </svg>
                </button>
              </span>
            </span>
            <div className="viewNumberTravel">
              <div className="adult">
                {" "}
                Adults
                <div className="plusAndMinus">
                  <i 
                  onClick={() => this.changeNumber("adult", "minus")}
                  className="fa fa-minus-circle" />
                  &nbsp;{numberInjoy.adult}&nbsp;
                  <i onClick={() => this.changeNumber("adult", "plus")}
                  className="fa fa-plus-circle" />
                </div>
              </div>
              <div className="children">
                {" "}
                Children
                <div className="plusAndMinus">
                  <i onClick={() => this.changeNumber("child", "minus")}
                  className="fa fa-minus-circle" />
                  &nbsp;{numberInjoy.children}&nbsp;
                  <i onClick={() => this.changeNumber("child", "plus")}
                  className="fa fa-plus-circle" />
                </div>
              </div>
              <p>€223 per person</p>
              <a href="javascript:void(0)">Apply</a>
            </div>
          </div>
        </div>
        <div className="LocalInfo-3-Zrh">
          <div className="LocalImages-1fJRM">
            <img
              alt
              src="https://withlocals-com-res.cloudinary.com/image/upload/w_56,h_56,c_fill,g_auto,q_auto,dpr_1.0,f_auto/49726c5301c4720ed18497075cd753cd"
              width={56}
              height={56}
              className="LocalAvatar-2w9Nv"
            />
            <img
              alt
              src="https://withlocals-com-res.cloudinary.com/image/upload/w_56,h_56,c_fill,g_auto,q_auto,dpr_1.0,f_auto/502ed371a533beceb145d4f52c96bd7c"
              width={56}
              height={56}
              className="LocalAvatar-2w9Nv"
            />
          </div>
          <div className="LocalInfoText-2C1wz">
            <span>
              <span data-translatekey="ExperiencePage.Footer.pickALocal">
                Pick one of the <span className="HighlightedText-1qv83">7</span>{" "}
                locals available on this tour
              </span>
            </span>
          </div>
        </div>
        <div className="BookButtonContainer-8-YC2">
          <button className="Button-2iSbC Gradient-9mayK FullWidth-pJfgw Large-1e1rx">
            Pick your favorite local
          </button>
        </div>
      </div>
    </div>
    <div className="content-right">
      <div className="intro">
        <ol className="Breadcrumbs">
          <li className="ListItem">
            <a className="Link-3Z1kp" href="">
              <span>Viet Nam</span>
            </a>
            ›
          </li>
          <li className="ListItem">
            <a className="Link-3Z1kp" href="">
              <span>Ha Noi</span>
            </a>
            ›
          </li>
          <li className="">
            <a className="Link-3Z1kp" href="">
              <span>Tours</span>
            </a>
            ›
          </li>
          <li className="ListItem">
            <a className="Link-3Z1kp" href="">
              <span>Food tours</span>
            </a>
            ›
          </li>
          <li className="ListItem">
            <span>HaNoi's Favorite Food Tour: 10 tastings</span>
          </li>
        </ol>
        <video controls>
          <source src="./video/Food_Tour-1_m8apyj.webm" type="video/mp4" />
        </video>
        <h2 className="titleTour">
          HaNoi's Favorite Food Tour: the 10 Tastings
        </h2>
        <p className="introduceTour">
          Ready to taste the best food in Lisbon? Get your taste buds dancing
          during the most delicious Lisbon food tour in town! Together with your
          foodie insider, enjoy 10 delicious tastings, and food memories to last
          you a lifetime. What are you waiting for? Tasty local food awaits!
        </p>
      </div>
      <div className="activities">
        <ul>
          <li>
            <i className="fa fa-map-marker" />
            <span>Ha Noi</span>
          </li>
          <li>
            <i className="fa fa-cutlery" aria-hidden="true"></i>
            <span>Food tour</span>
          </li>
          <li>
            <i className="fa fa-hourglass-end" />
            <span>3 hours</span>
          </li>
          <li>
            <i className="fa fa-bicycle" aria-hidden="true"></i>
            <span>Walking tour</span>
          </li>
          <li>
            <i className="fa fa-users" />
            <span>Private tour. Only you and your host</span>
          </li>
        </ul>
        <i className="fa fa-sticky-note" />
        <span>
          <strong>Including:</strong>Private guide • 8 tastings and 2 drinks • 2
          alcoholic drinks
        </span>
      </div>
      <div className="activities reason">
        <h2>6 reasons to book this tour</h2>
        <ul>
          <li>
            <i className="fa fa-check" />
            <p>Visit the charming old village of Sintra</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>Discover all the mysteries of Regaleira Palace and Gardens</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>Be amazed by the Arabic-style Monserrate Palace</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>Visit the most westerly point of Europe</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>100% personalized to your wishes</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>Discover the laid back seaside town of Cascais</p>
          </li>
        </ul>
      </div>
      {/* slideshow */}
      <div className="slideshow">
        <div className="slideshow-container">
          <h2>What you can expect</h2>
          {this.state.slideShow.map((link, i) => (
            <div className={`mySlides${i === this.state.currentIndex ? ' active' : ''}`} key={i}>
              <img src={link} />
            </div>
          ))}
          <a className="prev" onClick={this.handlePrev}>
            ❮
          </a>
          <a className="next" onClick={this.handleNext}>
            ❯
          </a>
        </div>
        <br />
        <div
          className="navigation"
          style={{ textAlign: "center", marginBottom: 30 }}
        >
        {this.state.slideShow.map((link, i) => (
          <span className={`dot${i === this.state.currentIndex ? ' active' : ''}`} key={i} onClick={() => this.handleChoose(i)} />
        ))}
        </div>
      </div>
      {/* end slideshow */}
      {/* plan */}
      <div className="plan">
        <h2>This is plan 3</h2>
        <p>
          Check out the plan below to see what you'll get up to with your local
          host.
        </p>
        <p /> Feel free to personalize this offer.
        <p />
        <div style={{ marginBottom: 30 }} />
        <div className="meetPoint">
          <i className="fa fa-map-marker-alt" />
          <div className="detailPlan">
            <h4>Meeting point</h4>
            <p>Curry 36 - Berlin</p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Classic Currywurst</h4>
            <p>
              Try it from a place with 35 years of tradition that became a
              culinary Berliner institution.
            </p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Schaumkuss</h4>
            <p>Try a delicious sweet treat</p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>A sweet Berliner (Pfannkuchen)</h4>
            <p>A Withlocals favorite that you will not forget!</p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Leberkäse mit Semmel</h4>
            <p>A hearty portion of a German specialty</p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Kartoffelpuffer</h4>
            <p>
              Authentic German street-food, prepared for 30 years, daily, by the
              same chef.
            </p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Original German Berlin beer</h4>
            <p>Time for a refreshment!</p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Berlin-style Boulette</h4>
            <p>
              Taste a Berlin-style Boulette served by an imbiss run by locals.
            </p>
          </div>
        </div>
        <div className="detail">
          <i className="fa fa-circle" />
          <div className="detailPlan">
            <h4>Baklava</h4>
            <p>Oriental sweets to enjoy the tour on a sweet note!/p&gt;</p>
          </div>
        </div>
        <button>Contact me to personalize this for you</button>
      </div>
      {/* end plan */}
      {/* good to know  */}
      <div className="activities reason shouldKnow">
        <h2>6 reasons to book this tour</h2>
        <ul>
          <h3>What's included</h3>
          <li>
            <i className="fa fa-check" />
            <p>Private guide</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>8 local food tastings</p>
          </li>
          <li>
            <i className="fa fa-check" />
            <p>2 alcoholic drinks: beer, local drinks</p>
          </li>
        </ul>
        <ul>
          <h3>Cancellation</h3>
          <li>
            <i className="fa fa-check" />
            <p>Free cancellation up to 14 days before experience date</p>
          </li>
          <li>
            <i className="fa fa-times" />
            <p>No refunds within 14 days of experience date.</p>
          </li>
        </ul>
      </div>
      {/* end good to know */}
      {/* List review */}
      <ul className="listReview">
        <h2>Reviews</h2>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
        <li>
          <div className="review">
            <div className="reviewContainer">
              <img
                className="defaultLogo"
                src="/img/defaultAvatarComment.webp"
                alt="logo"
              />
              <div className="reviewInfo">
                <div className="nickName">AnnaBanana</div>
                <div className="rating">
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                  <i className="fa fa-star" />
                </div>
                <div className="dateComment">1 October 2019</div>
              </div>
              <div className="commentDetails">
                You can go to a city and explore it one time, two times, maybe
                even lucky enough to go three times. You can research that city
                on the internet and get so much information. But, you will never
                know a city the way a local knows their city. Laura showed us so
                many hidden treasures in Barcelona that were not on the
                internet. We were with her for only three hours. Yet we saw so
                much and learned so much about Barcelona and Spain in that short
                period of time. Laura is an amazing guide. Her English is easy
                to understand. And she is truly a friendly, knowledgable person.
              </div>
              <span className="reviewTitle">Laura The Local!</span>
            </div>
          </div>
        </li>
      </ul>
      {/* Load Review button */}
      <div className="commentButton">
        <div id="loadMore">Show more reviews</div>
        <div id="showLess">Hide less reviews</div>
      </div>
      {/* End Review */}
    </div>
  </div>
  <div className="bookOffers tourversion">
    <h2>Other things to do in Ha Noi</h2>
    <ul>
      <li>
        <div className="sheet">
          <button className="unlike">
            <i className="fa fa-heart" />
          </button>
          <button className="like">
            <i className="far fa-heart" />
          </button>
          <div className="imageFigure">
            <img src="/img/1.jpg" alt="logo" width={42} height={42} />
          </div>
          <div className="experienceCard-details">
            <div className="experienceAvatarCardContainer">
              <div className="experienceAvatarCard1">
                <img src="/img/2.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="experienceAvatarCard2">
                <img src="/img/3.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="localAvailable">7 others locals available</div>
            </div>
            <span className="enjoy">
              Enjoy Hanoi with <span className="withName">Amadio</span>
            </span>
            <h3 className>Mysteries of Regaleira: Sintra</h3>
            <div className="price">
              <span>$57.50 pp</span>
              <span className="experienceCard-topDetails-bullet">●</span>
              <span className="experienceCard-topDetails-duration">
                8 hours
              </span>
              <span className="experienceCard-topDetails-bullet"> ●</span>
              <span data-translatekey="Experience.SubcategoryOrTag.day-trip">
                Day trip
              </span>
            </div>
            <div className="experienceCard-bottomDetails">
              <div className="rating">
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
              </div>
              <span className="colorShared">1249 |</span>
              <span className="colorShared">
                <i className="fa fa-bolt" /> |
              </span>
              <span className="colorShared">
                <i className="fa fa-car" />
              </span>
            </div>
          </div>
        </div>
      </li>
      <li>
        <div className="sheet">
          <button className="unlike">
            <i className="fa fa-heart" />
          </button>
          <button className="like">
            <i className="far fa-heart" />
          </button>
          <div className="imageFigure">
            <img src="/img/1.jpg" alt="logo" width={42} height={42} />
          </div>
          <div className="experienceCard-details">
            <div className="experienceAvatarCardContainer">
              <div className="experienceAvatarCard1">
                <img src="/img/2.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="experienceAvatarCard2">
                <img src="/img/3.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="localAvailable">7 others locals available</div>
            </div>
            <span className="enjoy">
              Enjoy Hanoi with <span className="withName">Amadio</span>
            </span>
            <h3 className>Mysteries of Regaleira: Sintra</h3>
            <div className="price">
              <span>$57.50 pp</span>
              <span className="experienceCard-topDetails-bullet">●</span>
              <span className="experienceCard-topDetails-duration">
                8 hours
              </span>
              <span className="experienceCard-topDetails-bullet"> ●</span>
              <span data-translatekey="Experience.SubcategoryOrTag.day-trip">
                Day trip
              </span>
            </div>
            <div className="experienceCard-bottomDetails">
              <div className="rating">
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
              </div>
              <span className="colorShared">1249 |</span>
              <span className="colorShared">
                <i className="fa fa-bolt" /> |
              </span>
              <span className="colorShared">
                <i className="fa fa-car" />
              </span>
            </div>
          </div>
        </div>
      </li>
      <li>
        <div className="sheet">
          <button className="unlike">
            <i className="fa fa-heart" />
          </button>
          <button className="like">
            <i className="far fa-heart" />
          </button>
          <div className="imageFigure">
            <img src="/img/1.jpg" alt="logo" width={42} height={42} />
          </div>
          <div className="experienceCard-details">
            <div className="experienceAvatarCardContainer">
              <div className="experienceAvatarCard1">
                <img src="/img/2.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="experienceAvatarCard2">
                <img src="/img/3.jpg" alt="logo" width={64} height={64} />
              </div>
              <div className="localAvailable">7 others locals available</div>
            </div>
            <span className="enjoy">
              Enjoy Hanoi with <span className="withName">Amadio</span>
            </span>
            <h3 className>Mysteries of Regaleira: Sintra</h3>
            <div className="price">
              <span>$57.50 pp</span>
              <span className="experienceCard-topDetails-bullet">●</span>
              <span className="experienceCard-topDetails-duration">
                8 hours
              </span>
              <span className="experienceCard-topDetails-bullet"> ●</span>
              <span data-translatekey="Experience.SubcategoryOrTag.day-trip">
                Day trip
              </span>
            </div>
            <div className="experienceCard-bottomDetails">
              <div className="rating">
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
                <i className="fa fa-star" />
              </div>
              <span className="colorShared">1249 |</span>
              <span className="colorShared">
                <i className="fa fa-bolt" /> |
              </span>
              <span className="colorShared">
                <i className="fa fa-car" />
              </span>
            </div>
          </div>
        </div>
      </li>
    </ul>
  </div>
  {/*  End Content  */}
</div>;


            </div>
        );
    }
}

export default Tour;