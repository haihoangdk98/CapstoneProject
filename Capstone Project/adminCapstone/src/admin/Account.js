import React, { Component } from "react";
import Config from "../Config";
import { Link } from "react-router-dom";

class Account extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hideButton: true,
      account: [],
      accountFiller: [],
      input:''
    };
  }

  loadData = async()=>{
    let { accountFiller } = this.state;
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "account/findAll",
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    const acc = await response.json();
    acc.map(data => {
      if (data.role === "TRAVELER") {
        accountFiller.push(data);
      }
    });
    this.setState({ accountFiller,account:acc });
  }

  async componentDidMount() {
    this.loadData();
  }


  handleFillter = letter => {
    let { account } = this.state;
    let accountFiller = [];
    let hideButton;
    account.map(data => {
      if (data.role === letter) {
        accountFiller.push(data);
      }
    });
    letter === "TRAVELER" ? (hideButton = true) : (hideButton = false);
    this.setState({ accountFiller, hideButton,letter,input:''});
  };
  
  handleChange = async (e) =>{
    let value =  e.target.value;
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "account/findAccountByNameAdmin?name="+value,
      autheticate
    );

    let data = await response.json();
    this.setState({accountFiller:data,letter:'GUIDER',hideButton:false,input:value});
  }

  handleActive = async (param,param2,index)=>{
    let {accountFiller} = this.state;
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "Guider/"+param2+"/"+param,
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    if(param2 === "Activate"){
      accountFiller[index].status = true;
      console.log(accountFiller[index]);
    }else{
     accountFiller[index].status = false;
     console.log(accountFiller[index]);
    }
    this.setState({accountFiller});
  }

  render() {
    let { accountFiller, hideButton,letter } = this.state;
    let dataAcc = accountFiller.map((data, index) => {
      if (data.role === "TRAVELER") {
        return (
          <tr role="row" className="odd" key={index}>
            <td className="sorting_1">{data.userName}</td>
            <td>{data.email}</td>
            <td>{data.role}</td>
          </tr>
        );
      } else {
        return (
          <tr role="row" className="odd" key={index}>
            <td className="sorting_1 triggerA" ><Link style={{color:'#e71575'}} to={'/guider/'+data.id}>{data.userName}</Link></td>
            <td>{data.email}</td>
            <td>{data.role}</td>
            <td>{data.status ? 'Active':'Deactive'}</td>
            <td>
              <span className="btn btn-primary btn-icon-split triggerA" onClick={()=>this.handleActive(data.id,"Activate",index)}>
                <span className="icon text-white-50">
                  <i className="fas fa-flag"></i>
                </span>
                <span className="text">Activate</span>
              </span>
            </td>
            <td>
              <span className="btn btn-danger btn-icon-split triggerA" onClick={()=>this.handleActive(data.id,"Deactivate",index)}>
                <span className="icon text-white-50">
                  <i className="fas fa-trash"></i>
                </span>
                <span className="text">Deactivate</span>
              </span>
            </td>
          </tr>
        );
      }
    });
    return (
      <div className="container-fluid">
        {/* DataTales Example */}
        <div className="card shadow mb-4">
          <div className="card-header py-3">
            <h6 className="m-0 font-weight-bold text-primary" style={{display:'inline'}}>
              Manage Account
            </h6>
            <form className="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search" style={{
                border:'1px #eaeaea solid',
                borderRadius: '6px'
            }}>
                      <div className="input-group">
                        <input
                          type="text"
                          className="form-control bg-light border-0 small"
                          placeholder="Search for guider..."
                          aria-label="Search"
                          aria-describedby="basic-addon2"
                          value={this.state.input}
                          onChange={this.handleChange}
                        />
                        <div className="input-group-append">
                          <button className="btn btn-primary" type="button">
                            <i className="fas fa-search fa-sm" />
                          </button>
                        </div>
                      </div>
                    </form>
          </div>
          <div className="card-body">
            <div className="table-responsive">
              <div
                id="dataTable_wrapper"
                className="dataTables_wrapper dt-bootstrap4"
              >
                <div
                  className="row"
                  style={{ textAlign: "center", marginBottom: "20px" }}
                >
                  <div className="role">
                    <span
                      onClick={() => this.handleFillter("TRAVELER")}
                      className="btn btn-primary btn-icon-split traveler-button"
                    >
                      <span className="text">TRAVELER</span>
                    </span>
                    <span
                      onClick={() => this.handleFillter("GUIDER")}
                      className="btn btn-primary btn-icon-split guider-button"
                    >
                      <span className="text">GUIDER</span>
                    </span>
                  </div>
                </div>
                <div className="row">
                  <div className="col-sm-12">
                    <table
                      className="table table-bordered dataTable"
                      id="dataTable"
                      width="100%"
                      cellSpacing={0}
                      role="grid"
                      aria-describedby="dataTable_info"
                      style={{ width: "100%" }}
                    >
                      <thead>
                        <tr role="row">
                          <th
                            className="sorting_asc"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-sort="ascending"
                            aria-label="Name: activate to sort column descending"
                            style={{ width: 270 }}
                          >
                            User name
                          </th>
                          <th
                            className="sorting"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-label="Position: activate to sort column ascending"
                            style={{ width: 401 }}
                          >
                            Email
                          </th>
                          <th
                            className="sorting_asc"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-sort="ascending"
                            aria-label="Name: activate to sort column descending"
                            style={{ width: 200 }}
                          >
                            Role
                          </th>
                          {
                            letter === "GUIDER" ?  <th
                            className="sorting_asc"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-sort="ascending"
                            aria-label="Name: activate to sort column descending"
                            style={{ width: 200 }}
                          >
                            Status
                          </th> : null
                          }
                          {hideButton ? (
                            null
                          ) : (
                            <th
                              className="sorting"
                              tabIndex={0}
                              aria-controls="dataTable"
                              rowSpan={1}
                              colSpan={1}
                              aria-label="Office: activate to sort column ascending"
                              style={{ width: 150 }}
                            >
                              Activate
                            </th>
                          )}
                          {hideButton ? (
                            null
                          ) : (
                            <th
                              className="sorting"
                              tabIndex={0}
                              aria-controls="dataTable"
                              rowSpan={1}
                              colSpan={1}
                              aria-label="Office: activate to sort column ascending"
                              style={{ width: 150 }}
                            >
                              Deactivate
                            </th>
                          )}
                        </tr>
                      </thead>
                      <tbody>{dataAcc}</tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default Account;
