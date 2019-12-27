import React, { Component } from 'react';
import { BrowserRouter as Router, Switch, Route, Link } from "react-router-dom";

class template extends Component {
  constructor(props) {
    super(props);
  
  }
    render() {
        return (

            <div id="wrapper">
              {/* Sidebar */}
              <ul
                className="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion"
                id="accordionSidebar"
              >
                {/* Sidebar - Brand */}
                <a
                  className="sidebar-brand d-flex align-items-center justify-content-center"
                  href="index.html"
                >
                  <div className="sidebar-brand-icon rotate-n-15">
                    <i className="fas fa-laugh-wink" />
                  </div>
                  <div className="sidebar-brand-text mx-3">
                    <Link to={'/'} style={{color:'#fff'}}>Admin </Link>
                  </div>
                </a>
                {/* Heading */}
        
                <hr className="sidebar-divider" />
                {/* Heading */}
                <div className="sidebar-heading">Addons</div>
                <Link className="nav-item" to={'/guiderContract'}>
                  <span className="nav-link triggerA" href="charts.html">
                    <i className="fas fa-fw fa-chart-area" />
                    <span>Manage guider contract</span>
                  </span>
                </Link>
                {/* Nav Item - Tables */}
                <Link className="nav-item" to={'/account'}>
                  <span className="nav-link triggerA" >
                    <i className="fas fa-fw fa-table" />
                    <span>Manage account</span>
                  </span>
                </Link>
                <Link className="nav-item" to={'/cateNLoc'}>
                  <span className="nav-link triggerA" href="tables.html">
                    <i className="fas fa-fw fa-table" />
                    <span>Manage category and location</span>
                  </span>
                </Link>
                <Link className="nav-item" to={'/chartRevenue'}>
                  <span className="nav-link triggerA" href="tables.html">
                    <i className="fas fa-fw fa-table" />
                    <span>Statistic of revenue by month</span>
                  </span>
                </Link>
                <Link className="nav-item" to={'/chartTrip'}>
                  <span className="nav-link triggerA" href="tables.html">
                    <i className="fas fa-fw fa-table" />
                    <span>Statistic of completed trip by month</span>
                  </span>
                </Link>
                {/* Divider */}
                <hr className="sidebar-divider d-none d-md-block" />
              </ul>
              {/* End of Sidebar */}
              {/* Content Wrapper */}
              <div id="content-wrapper" className="d-flex flex-column">
                {/* Main Content */}
                <div id="content">
                  {/* Topbar */}
                  <nav className="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
                    {/* Sidebar Toggle (Topbar) */}
                    <button
                      id="sidebarToggleTop"
                      className="btn btn-link d-md-none rounded-circle mr-3"
                    >
                      <i className="fa fa-bars" />
                    </button>
                    {/* Topbar Search */}
                    <form className="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search">
                      <div className="input-group">
                        <input
                          type="text"
                          className="form-control bg-light border-0 small"
                          placeholder="Search for..."
                          aria-label="Search"
                          aria-describedby="basic-addon2"
                        />
                        <div className="input-group-append">
                          <button className="btn btn-primary" type="button">
                            <i className="fas fa-search fa-sm" />
                          </button>
                        </div>
                      </div>
                    </form>
                    {/* Topbar Navbar */}
                    <ul className="navbar-nav ml-auto">
                      {/* Nav Item - Search Dropdown (Visible Only XS) */}
                      <li className="nav-item dropdown no-arrow d-sm-none">
                        <a
                          className="nav-link dropdown-toggle"
                          href="#"
                          id="searchDropdown"
                          role="button"
                          data-toggle="dropdown"
                          aria-haspopup="true"
                          aria-expanded="false"
                        >
                          <i className="fas fa-search fa-fw" />
                        </a>
                        {/* Dropdown - Messages */}
                        <div
                          className="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in"
                          aria-labelledby="searchDropdown"
                        >
                          <form className="form-inline mr-auto w-100 navbar-search">
                            <div className="input-group">
                              <input
                                type="text"
                                className="form-control bg-light border-0 small"
                                placeholder="Search for..."
                                aria-label="Search"
                                aria-describedby="basic-addon2"
                              />
                              <div className="input-group-append">
                                <button className="btn btn-primary" type="button">
                                  <i className="fas fa-search fa-sm" />
                                </button>
                              </div>
                            </div>
                          </form>
                        </div>
                      </li>
                    
                      {/* Nav Item - User Information */}
                      <li className="nav-item dropdown no-arrow">
                        <a
                          className="nav-link dropdown-toggle"
                          href="#"
                          id="userDropdown"
                          role="button"
                          data-toggle="dropdown"
                          aria-haspopup="true"
                          aria-expanded="false"
                        >
                          <span className="mr-2 d-none d-lg-inline text-gray-600 small">
                            Admin
                          </span>
                          <img
                            className="img-profile rounded-circle"
                            src="./images/account.jpg"
                          />
                        </a>
                        {/* Dropdown - User Information */}
                        <div
                          className="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                          aria-labelledby="userDropdown"
                        >
                          <div className="dropdown-divider" />
                          <span
                            className="dropdown-item triggerA"
                            data-toggle="modal"
                            data-target="#logoutModal"
                            onClick={(e)=>{this.props.handleClick(e)}}
                          >
                            <i className="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400" />
                            Logout
                          </span>
                        </div>
                      </li>
                    </ul>
                  </nav>
                  {/* End of Topbar */}
                  {/* Begin Page Content */}
                  

                {/* Code reactjs here */}



                  {/* End of Main Content */}
                </div>
                {/* End of Content Wrapper */}
              </div>
              {/* End of Page Wrapper */}
              {/* Scroll to Top Button*/}
              <a className="scroll-to-top rounded" href="#page-top">
                <i className="fas fa-angle-up" />
              </a>
              {/* Logout Modal*/}
              <div
                className="modal fade"
                id="logoutModal"
                tabIndex={-1}
                role="dialog"
                aria-labelledby="exampleModalLabel"
                aria-hidden="true"
              ></div>
            </div>
          );
    }
}

export default template;