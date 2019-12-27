import React, { Component } from 'react';
import "font-awesome/css/font-awesome.min.css";
import $ from 'jquery';
// import iconMain from '../../public/icon/iconMain.jpg';
class Footer extends Component {
    componentDidMount() {
        $("head").append('<link href="/css/footer.css" rel="stylesheet"/>');
    }
    render() {
        return (
            <footer>
                <div className="footer-content">
                    <div className="socialMedia">
                        <div className="webLogo">
                            <a href="/">
                                <i className="fa fa-arrows" aria-hidden="true" />
                            </a>
                            Travel

                        </div>
                    </div>
                    <div className="webInfo">
                        <div>
                            <h4>Contact us</h4>
                            <ul>
                                <li>
                                    <a>Phone:0969449743</a>
                                </li>
                                <li>
                                    <a>Mail:Longthse04935@fpt.edu.vn</a>
                                </li>
                                <li>
                                    <a>Help</a>
                                </li>
                            </ul>
                        </div>
                        <div>
                            <h4>Support</h4>
                            <ul>
                                <li>
                                    <a>Help Center for hosts</a>
                                </li>
                                <li>
                                    <a>Help Center for travelers</a>
                                </li>
                                <li>
                                    <a>Pravicy Policy</a>
                                </li>
                                <li>
                                    <a>Term and Conditions</a>
                                </li>
                                <li>
                                    <a>Cancelation policy for guests</a>
                                </li>
                                <li>
                                    <a>Cancelation policy for hosts</a>
                                </li>
                            </ul>
                        </div>
                        <div>
                            <h4>About my web</h4>
                            <ul>
                                <li>
                                    <a>Our story</a>
                                </li>
                                <li>
                                    <a>Jobs</a>
                                </li>
                                <li>
                                    <a>Press</a>
                                </li>
                                <li>
                                    <a>Blog</a>
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