import React from 'react';
import "font-awesome/css/font-awesome.min.css";
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";
import Config from '../Config';
import Rated from './Rated';
import { connect } from 'react-redux';
import SweetAlert from 'react-bootstrap-sweetalert';
import $ from 'jquery';
class GudierInShort extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            guider: {
                languages: ['']
            },
            alert: null
        };
    }

    async componentDidMount() {
        try {
            const response = await fetch(Config.api_url + "Guider/" + this.props.guiderId, {
                method: "GET",
                mode: "cors",
                credentials: "include"
            });
            if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
            const guider = await response.json();
            //   console.log(guider);
            this.setState({ guider });
        } catch (err) {
            console.log(err);
        }

    }
    
    render() {
        let guide = this.state.guider;
        let languages = '';
        for (var i = 0; i < guide.languages.length; i++) {
            if (i + 1 === guide.languages.length) {
                languages += guide.languages[i].toUpperCase();
            } else {
                languages += guide.languages[i].toUpperCase() + ',';
            }
        }
        
     

        return (

            <div className="profile-box">
                {this.state.alert}
                <div className="pb-header header-stick">
                    <div className="header-pb">
                        <Link to={`/guider/${guide.guider_id}`}><h1 className="TitlePb TileStickyPb">{guide.first_name + " " + guide.last_name}</h1></Link>
                        <Rated number={guide.rated} />
                    </div>
                    <div>
                        <img className="pf-avatar"
                            src={guide.avatar} />

                    </div>
                </div>
                <p className="ListItem">
                    <span className="ListItemIcon">
                        <i className="fa fa-map-marker"></i>
                    </span>
                    <span className="ListItemText">
                        I live in {guide.city}
                    </span>
                </p>
                <p className="ListItem">
                    <span className="ListItemIcon">
                        <i className="fa fa-globe"></i>
                    </span>
                    <span className="ListItemText">
                        I speak {languages}
                    </span>
                </p>
                <p className="ListItem">
                    <span className="ListItemIcon">
                        <i className="fa fa-heart"></i>
                    </span>
                    <span className="ListItemText">
                        My passions are: {guide.passion}
                    </span>
                </p>
                <p className="ListItem">
                    <span className="ListItemIcon">
                        <i className="fa fa-shield" aria-hidden="true"></i>
                    </span>
                    <span className="ListItemText">Verified</span>
                </p>
               

            </div>
        );
    }

}
function mapStateToProps(state) {
    const user = state.user;

    return { user };
}
export default connect(mapStateToProps)(GudierInShort);