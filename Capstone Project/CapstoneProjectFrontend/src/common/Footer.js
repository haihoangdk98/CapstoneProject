import React, { Component } from "react";
import "font-awesome/css/font-awesome.min.css";
import $ from "jquery";
// import iconMain from '../../public/icon/iconMain.jpg';
class Footer extends Component {
  componentDidMount() {
    // $("head").append('<link href="/css/footer.css" rel="stylesheet"/>');
  }
  render() {
    return (
      <footer>
        <div className="footer-content">
          <div className="socialMedia">
            <div className="webLogo">Travel with Locals</div>
          </div>
          <div className="webInfo">
            <div>
              <h4>About us</h4>
              <ul>
                <li>
                  <a>
                    Travel with Locals is all about the activities that happen
                    when you’re not lounging around your hotel room, and they
                    can start from the moment you arrive at your destination.
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4>Contact us</h4>
              <ul>
                <li>
                  <a><i style={{marginRight:'10px'}} class="fa fa-phone" aria-hidden="true"></i>+84 969449743</a>
                </li>
                <li>
                  <a><i style={{marginRight:'10px'}} class="fa fa-map-marker" aria-hidden="true"></i>Address: Km29, Thanglong boulevard, Thachthat, Hanoi, Vietnam</a>
                </li>
                <li>
                  <a><i style={{marginRight:'10px'}} class="fa fa-envelope-o" aria-hidden="true"></i>Email: findguider@gmail.com</a>
                </li>
              </ul>
            </div>
            <div>
              <h4 style={{marginLeft:'10px'}}>Reach us</h4>
              <ul className="reachUs">
                <li>
                  <a><i class="fa fa-facebook-official" aria-hidden="true"></i></a>
                </li>
                <li>
                  <a><i class="fa fa-instagram" aria-hidden="true"></i></a>
                </li>
                <li>
                  <a><i class="fa fa-twitter-square" aria-hidden="true"></i></a>
                </li>
              </ul>
            </div>
          </div>
          <div />
        </div>
      </footer>
    );
  }
}

export default Footer;
