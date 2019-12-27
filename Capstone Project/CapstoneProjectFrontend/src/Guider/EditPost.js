import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import Navbar from '../Nav/Navbar';
import Footer from '../common/Footer';
import $ from "jquery";
import ReactDOMServer from 'react-dom/server';
import Image from './Image';
import SweetAlert from 'react-bootstrap-sweetalert';
import Config from '../Config';
class EditPost extends Component {
    constructor(props) {
        super(props);

        let initLocation = [{
            location_id: 1,
            location: "Phu Quoc"
        }];
        let initCategory = [{}];
        let initPlan = {
            meetingPoint: "",
            plan: []
        }

        this.state = {
            alert: null,
            post_id: 0,
            locations: initLocation,
            categories: initCategory,
            activities: [{
                brief: "",
                detail: ""
            }],
            total_hour: 1,
            images: [],
            services: [""],
            reasons: [""],
            plan: initPlan,
            "title": "",
            "video_link": "",
            "description": "",
            meeting_point: "",
            "location": "",
            "category": "",
            "reason": "",
            "price": ""
        };
        this.addService = this.addService.bind(this);
        this.removeService = this.removeService.bind(this);
        this.addActivity = this.addActivity.bind(this);
        this.removeActivity = this.removeActivity.bind(this);
        this.formHandler = this.submitForm.bind(this);
        this.inputOnChange = this.inputOnChange.bind(this);
        this.addReason = this.addReason.bind(this);
        this.removeReason = this.removeReason.bind(this);
        this.onCancel = this.onCancel.bind(this);
        this.notification = this.notification.bind(this);
        this.onNotification = this.onNotification.bind(this);
        this.statusProfile = this.statusProfile.bind(this);
    }

    resetForm = (eve) => {
        eve.preventDefault();
        this.setState({
            activities: [{
                brief: "",
                detail: ""
            }],
            images: [],
            services: [""],
            reasons: [""],
            plan: {
                meetingPoint: "",
                plan: []
            },
            "title": "",
            "video_link": "",
            total_hour: 1,
            "description": "",
            meeting_point: "",
            "location": "",
            "category": "",
            "reason": "",
            "price": ""
        });
    }



    removeReason(eve) {
        const copy = this.state;
        const dom = ReactDOM.findDOMNode(this);
        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".reason");
            let reasons = [];
            for (let i = 0; i < acts.length; i++) {
                if (i == eve.target.id) continue;
                reasons.push(acts[i].value);
            }
            for (let i = 0; i < reasons.length; i++) {
                acts[i].value = reasons[i];
            } console.log(reasons);
            copy.reasons = reasons;
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }
    }

    addReason() {
        const copy = this.state;

        const dom = ReactDOM.findDOMNode(this);
        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".reason");
            let reasons = [];
            for (let i = 0; i < acts.length; i++) {
                reasons.push(acts[i].value);
            }
            reasons.push("");
            copy.reasons = reasons;
            // console.log(copy.services);
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }
    }

    inputOnChange(eve) {
        let nam = eve.target.name;
        let val = eve.target.value;
        this.setState({ [nam]: val });
    }

    async componentDidMount() {

        $("head").append('<link href="/css/editPost.css" rel="stylesheet"/>');
        const copy = Object.assign({}, this.state);
        try {
            const responseLocation = await fetch(Config.api_url + "location/findAll");
            const responseCategory = await fetch(Config.api_url + "category/findAll");
            if (!responseCategory.ok) { throw Error(responseCategory.status + ": " + responseCategory.statusText); }
            if (!responseLocation.ok) { throw Error(responseLocation.status + ": " + responseLocation.statusText); }
            const location = await responseLocation.json();
            const category = await responseCategory.json();
            copy.locations = location;
            copy.categories = category;
            const post = await fetch(Config.api_url + "guiderpost/findSpecificPost?post_id=" + this.props.match.params.post,
                {
                    method: "GET",
                    mode: "cors",
                    credentials: "include",
                    headers: {
                        'Accept': 'application/json'
                    },
                });
            if (!post.ok) {
                window.location.href = "/page404"
                throw Error(post.status + ": " + post.statusText);
            }
            const edit = await post.json();
            //console.log(edit);
            const plans = await fetch(Config.api_url + "plan/" + this.props.match.params.post,
                {
                    method: "GET",
                    mode: "cors",
                    credentials: "include",
                    headers: {
                        'Accept': 'application/json'
                    },
                });
            copy.post_id = edit.post_id;
            copy.title = edit.title;
            copy.video_link = edit.video_link;
            copy.picture_link = edit.picture_link;
            copy.total_hour = edit.total_hour;
            copy.description = edit.description;
            copy.services = edit.including_service;
            copy.location = edit.location;
            copy.category = edit.category;
            copy.price = edit.price;
            copy.reasons = this.parseReason(edit.reasons);
            const plan = await plans.json();
            //console.log(plan);
            let imgs = [];
            for (let i = 0; i < edit.picture_link.length; i++) {
                imgs.push(await fetch(edit.picture_link[i])
                    .then(response => response.blob())
                    .then(blob => new Promise((resolve, reject) => {
                        const reader = new FileReader();
                        reader.readAsDataURL(blob);
                        reader.onload = () => resolve(reader.result);
                        reader.onerror = error => reject(error);
                    }))
                    );
            }
            copy.images = imgs;
            copy.meeting_point = plan.meeting_point;
            copy.activities = this.parsePlan(plan.detail);
            this.setState(await copy);

        } catch (err) {
            console.log(err);
        }

    }

    parseReason(html) {
        let detailRegex = /(<p>).+?(<\/p>)/g;
        let m;
        let reasons = [];
        do {

            m = detailRegex.exec(html);
            if (m) {
                reasons.push(m[0].replace("<p>", "").replace("</p>", ""));
            }
        } while (m);
        return reasons;
    }

    parsePlan(html) {
        let briefRegex = /(<h4>).+?(<\/h4>)/g;
        let detailRegex = /(<p>).+?(<\/p>)/g;
        let m;
        let briefs = [];
        let details = [];

        do {
            m = briefRegex.exec(html);
            if (m) {
                briefs.push(m[0].replace("<h4>", "").replace("</h4>", ""));
            }
            m = detailRegex.exec(html);
            if (m) {
                details.push(m[0].replace("<p>", "").replace("</p>", ""));
            }
        } while (m);
        let acts = [];
        for (let i = 0; i < briefs.length; i++) {
            acts.push({ brief: briefs[i], detail: details[i] });
        }
        return acts;
    }
    onCancel() {
        this.setState({
            alert: null
        });
    }
    //notification khi booking failed or success
    onNotification() {
        this.setState({ alert: null });
    }

    notification(notification) {
        const getAlert = () => (
            <SweetAlert
                warning
                confirmBtnText="Close"
                confirmBtnBsStyle="danger"
                title="Operation fails"
                onConfirm={() => this.onNotification()}
            >
                {notification}
            </SweetAlert>
        );

        this.setState({
            alert: getAlert()
        });
    }


    statusProfile(message) {
        const getAlert = () => (
            <SweetAlert
                success
                title="Done!"
                onConfirm={() => this.onNotification()}
            >
                {message}
            </SweetAlert>
        );

        this.setState({
            alert: getAlert()
        });
    }
    async submitForm(eve) {
        eve.preventDefault();
        const copy = Object.assign({}, this.state);
        const dom = ReactDOM.findDOMNode(this);


        let location = 1;
        let cate = 1;
        if (dom instanceof HTMLElement) {
            //query location
            let lo = dom.querySelector("#inputGroupSelect01");
            location = lo.options[lo.selectedIndex].value;
            //query cate
            lo = dom.querySelector("#inputGroupSelect02");
            cate = lo.options[lo.selectedIndex].value;

        } else {
            console.log("find DOM do not work");
        }
        let initPost = await {
            post_id: copy.post_id,
            "title": copy.title,
            "video_link": copy.video_link,
            "picture_link": this.state.images,
            "total_hour": copy.total_hour,
            "description": copy.description,
            "including_service": copy.services,
            "active": true,
            "location": location,
            "category": cate,
            "price": copy.price,
            "rated": 3,
            "reasons": ReactDOMServer.renderToString(this.reasonToHTML(copy.reasons))
        };
        let plan = ReactDOMServer.renderToString(this.planToHTML(copy.activities));
        console.log(initPost);
        console.log(plan);
        try {
            let response = await fetch(Config.api_url + "guiderpost/update/post",
                {
                    method: "POST",
                    mode: "cors",
                    credentials: "include",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(initPost)
                }
            );
            if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
            const id = await response.text();
            console.log(id);
            let plans = await {
                meeting_point: copy.meeting_point,
                detail: plan,
                post_id: id,
            };
            response = await fetch(Config.api_url + "plan/update",
                {
                    method: "POST",
                    mode: "cors",
                    credentials: "include",
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(plans)
                }
            );
            if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
            this.statusProfile("Thank you, your post has showed up");
        } catch (err) {
            console.log(err);
            this.notification("Sorry! Something went wrong, please try again later");
        }
    }

    planToHTML = acts => {
        return (
            acts.map((act, index) =>
                <div className="detail">
                    <i key={index} className="fa fa-circle"></i>
                    <div className="detailPlan">
                        <h4>{act.brief}</h4>
                        <p>{act.detail}</p>
                    </div>
                </div>)

        );
    }

    reasonToHTML = reasons => {
        return (<div className="activities reason">
            <h2>Reasons to book this tour</h2>
            <ul>
                {reasons.map((reason, index) =>
                    <li key={index}><i className="fa fa-check"></i>
                        <p>{reason}</p>
                    </li>
                )}


            </ul>
        </div>);
    }

    fromDataURL = async url => {
        return await fetch(url)
            .then(response => response.blob())
            .then(blob => new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.readAsDataURL(blob);
                reader.onload = () => resolve(reader.result);
                reader.onerror = error => reject(error);
            })

            );
    }

    toBase64 = file => new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });

    addService() {
        const copy = this.state;

        const dom = ReactDOM.findDOMNode(this);
        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".service");
            let services = [];
            for (let i = 0; i < acts.length; i++) {
                services.push(acts[i].value);
            }

            services.push("");
            copy.services = services;
            // console.log(copy.services);
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }
    }

    addActivity() {
        const copy = this.state;

        const dom = ReactDOM.findDOMNode(this);
        //document.querySelectorAll(".coverContent")[0].querySelector("input[name='detail']").value
        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".coverContent");

            let activities = [];
            for (let i = 0; i < acts.length; i++) {
                let brief = acts[i].querySelector("input[name='brief']").value;
                let detail = acts[i].querySelector("textarea[name='detail']").value;
                activities.push({
                    brief: brief,
                    detail: detail
                });
            }
            activities.push({
                brief: "",
                detail: ""
            });
            copy.activities = activities;
            //console.log(copy.activities);
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }
    }
    removeService(eve) {
        const copy = this.state;

        const dom = ReactDOM.findDOMNode(this);
        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".service");
            // console.log(acts);
            let services = [];
            for (let i = 0; i < acts.length; i++) {
                if (i == eve.target.id) continue;
                services.push(acts[i].value);
            }
            for (let i = 0; i < services.length; i++) {
                acts[i].value = services[i];
            }
            //console.log(services);
            copy.services = services;
            //console.log(eve.target.id);
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }

    }

    removeActivity(eve) {
        const copy = this.state;

        const dom = ReactDOM.findDOMNode(this);

        if (dom instanceof HTMLElement) {
            const acts = dom.querySelectorAll(".coverContent");

            let activities = [];
            for (let i = 0; i < acts.length; i++) {
                if (i == eve.target.id) continue;
                let brief = acts[i].querySelector("input[name='brief']").value;
                let detail = acts[i].querySelector("textarea[name='detail']").value;
                activities.push({
                    brief: brief,
                    detail: detail
                });
            }
            for (let i = 0; i < activities.length; i++) {
                acts[i].querySelector("input[name='brief']").value = activities[i].brief;
                acts[i].querySelector("textarea[name='detail']").value = activities[i].detail;
            }

            copy.activities = activities;
            // console.log(eve.target.id);
            this.setState(copy);
        } else {
            console.log("find DOM do not work");
        }
    }
    showImage = async (eve) => {
        let image = [];
        const dom = ReactDOM.findDOMNode(this);
        if (dom instanceof HTMLElement) {

            //query file
            const files = dom.querySelector(".filePicture").files;
            //console.log(files);

            for (let i = 0; i < files.length; i++) {
                image.push(await this.toBase64(files[i]));
            }
            dom.querySelector(".filePicture").value = "";
            //console.log(await image.length);
            this.setState({ images: this.state.images.concat(image) });
        } else {
            console.log("find DOM do not work");
        }
    }

    deleteImg = (index) => {
        let copy = Object.assign([], this.state.images);
        //console.log(copy);
        copy.splice(index, 1);
        this.setState({ images: copy });
        //console.log(this.state.images);
    }

    render() {
        //  console.log(this.state.images);
        let locationOption = this.state.locations.map((location, index) =>
            <option value={location.location_id} key={index} selected={(location.location === this.state.location)}>{location.location}</option>
        );
        let categoryOption = this.state.categories.map((cate, index) =>
            <option value={cate.category_id} key={index} selected={(cate.category === this.state.category)} >{cate.category}</option>
        );

        let serviceInput = this.state.services.map((service, index) =>
            <div className="dropdownCoverSelect" key={index}>

                <input className="dropdown-select service" type="text" required onChange={(eve) => { this.state.services[index] = eve.target.value; }} defaultValue={this.state.services[index]} />
                <button type="button" className="btn-add-service" onClick={this.removeService} id={index}><i className="fa fa-trash-o" aria-hidden="true"></i></button>

            </div>
        );

        let actInput = this.state.activities.map((act, index) =>
            <div className="activitiesInput" key={index}>
                <div className="coverContent" key={index}>
                    <div className="brief">Brief:<input type="text" name="brief" onChange={(eve) => { act.brief = eve.target.value; }} defaultValue={act.brief} required /></div>
                    <div className="detail">Detail:<textarea rows={4} cols={50} type="textarea" required name="detail" onChange={(eve) => { act.detail = eve.target.value; }} defaultValue={act.detail} /></div>
                    <button type="button" className="btn-add-service" onClick={this.removeActivity} id={index}><i className="fa fa-trash-o" aria-hidden="true"></i></button>
                </div>
            </div>
        );

        let reasonInput = this.state.reasons.map((reason, index) =>
            <div className="dropdownCoverSelect" key={index}>

                <input className="dropdown-select reason" type="text" required onChange={(eve) => { this.state.reasons[index] = eve.target.value; }} defaultValue={this.state.reasons[index]} />
                <button type="button" className="btn-add-service" onClick={this.removeReason} id={index}><i className="fa fa-trash-o" aria-hidden="true"></i></button>

            </div>
        );

        return (
            <div>
                <div className="container">
                    {this.state.alert}
                    <div className="row m-y-2">
                        {/* edit form column */}
                        <div className="col-lg-12 text-lg-center">
                            <h2>Edit Post</h2>
                            {/* {service} */}
                        </div>
                        <div className="col-lg-12 push-lg-4 personal-info">
                            <form role="form" onSubmit={this.formHandler}>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Location:</label>
                                    <div className="col-lg-8">
                                        <select className="custom-select" id="inputGroupSelect01" defaultValue={this.state.location} >
                                            {locationOption}
                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row">

                                    <label className="col-lg-4 col-form-label form-control-label">Category:</label>
                                    <div className="col-lg-8">
                                        <select className="custom-select" id="inputGroupSelect02" defaultValue={this.state.category} >
                                            {categoryOption}
                                        </select>
                                    </div>
                                </div>
                                <div className="form-group row">

                                    <label className="col-lg-4 col-form-label form-control-label">Trip Fee ($):</label>

                                    <div className="col-lg-8">
                                        <input onChange={this.inputOnChange} className="form-control" type="number" name="price"
                                            defaultValue={this.state.price} min="5" max="5000" required />
                                    </div>
                                </div>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Post title:</label>

                                    <div className="col-lg-8">
                                        <input onChange={this.inputOnChange} className="form-control" type="text" name="title"
                                            required defaultValue={this.state.title} />
                                    </div>
                                </div>
                                <div className="form-group row">

                                    <label onChange={this.inputOnChange} className="col-lg-4 col-form-label form-control-label">Introduce video link:</label>

                                    <div className="col-lg-8">
                                        <input onChange={this.inputOnChange} className="form-control" type="url" name="video_link"
                                            required defaultValue={this.state.video_link} />
                                    </div>
                                </div>
                                <div className="form-group row pictures">
                                    <label className="col-lg-4 col-form-label form-control-label">Introduce Pictures:</label>

                                    <div className="col-lg-7" id="picInput">
                                        <input

                                            className="filePicture"
                                            type="file"

                                            accept="image/png, image/jpeg. image/jpg"
                                            onChange={this.showImage}
                                            multiple
                                        />
                                        <Image bases={this.state.images} deleteImg={this.deleteImg} />
                                    </div>

                                </div>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Estimate trip duration (hours):</label>

                                    <div className="col-lg-8">
                                        <input onChange={this.inputOnChange} value={`${this.state.total_hour}`} name="total_hour" className="form-control "
                                            type="number" required min="1" max="24" />
                                    </div>
                                </div>
                                <div className="form-group row">

                                    <label className="col-lg-4 col-form-label form-control-label">Description your trip:</label>

                                    <div className="col-lg-8">
                                        <textarea onChange={this.inputOnChange} name="description" className="form-control" required defaultValue={this.state.description} />
                                    </div>
                                </div>
                                <div className="form-group row">

                                    <label className="col-lg-4 col-form-label form-control-label">Including service:</label>
                                    {/* <div className="col-lg-7" id="includeServiceCover"></div> */}
                                    <button type="button" className="style_BtnAdd" id="includeService" onClick={this.addService}>+</button>

                                </div>


                                <div className="include-service" >
                                    {serviceInput}
                                </div>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Meeting point:</label>

                                    <div className="col-lg-8">
                                        <input onChange={this.inputOnChange} name="meeting_point" className="form-control" required type="text" defaultValue={this.state.meeting_point} />
                                    </div>
                                </div>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Activities in trip:</label>

                                    <div className="col-lg-7"></div>

                                    <button type="button" className="style_BtnAdd" id="activitiesAdd" onClick={this.addActivity}>+</button>
                                </div>

                                <div className="">
                                    {actInput}
                                </div>
                                <div className="form-group row">
                                    <label className="col-lg-4 col-form-label form-control-label">Messages to travelers:</label>

                                    <div className="col-lg-7"></div>
                                    <button type="button" className="style_BtnAdd" id="reasonAdd" onClick={this.addReason}>+</button>
                                </div>

                                <div className="">
                                    {reasonInput}
                                </div>
                                <div className="form-group row">
                                    <div className="submit_btn">
                                        <input
                                            type="reset"
                                            className="btn btn-primary"
                                            defaultValue="Reset Form"
                                            onClick={this.resetForm}
                                        />
                                        <input
                                            type="submit"
                                            className="btn btn-primary"
                                            defaultValue="Save Changes"
                                        />
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default EditPost;