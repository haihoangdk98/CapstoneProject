import React, { Component } from "react";
import Config from "../Config";
import {Link } from "react-router-dom";
class PostGuider extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data:[]
    };
  }
  loadData = async () =>{
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "guiderpost/postOfOneGuiderAdmin?guider_id="+this.props.match.params.guider_id,
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    const data = await response.json();
    this.setState({data});
  }
  async componentDidMount() {
   this.loadData();
  };

  handleAccepct = async (param,index)=>{
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "guiderpost/authorizePost?post_id="+param,
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    let {data} = this.state;
    data[index].authorized = !data[index].authorized;
    this.setState({data});
  }

  render() {
    let { data} = this.state;
    let dataShow = data.map((data, index) => (
      <tr role="row" className="odd" key={index}>
      <td className="sorting_1 triggerA" ><Link to={'/review/'+data.post_id} style={{color:'#e71575'}}>{data.title}</Link></td>
      <td>{data.description}</td>
      <td>{data.reasons}</td>
      <td>{JSON.stringify(data.authorized).toUpperCase()}</td>
      { data.authorized ?
	  <td>
          <span className="btn btn-danger btn-icon-split triggerA" onClick={()=>{this.handleAccepct(data.post_id,index)}}>
            <span className="icon text-white-50">
              <i className="fas fa-trash"></i>
            </span>
            <span className="text">Deactivate</span>
          </span>
        </td>
        : 
        <td>
          <span className="btn btn-primary btn-icon-split triggerA" onClick={()=>{this.handleAccepct(data.post_id,index)}}>
            <span className="icon text-white-50">
              <i className="fas fa-flag"></i>
            </span>
            <span className="text">Activate</span>
          </span>
        </td>
      }
        
    </tr>
    ));
    return (
      <div className="container-fluid">
        {/* DataTales Example */}
        <div className="card shadow mb-4">
          <div className="card-header py-3">
            <h6 className="m-0 font-weight-bold text-primary">
              All trip
            </h6>
          </div>
          <div className="card-body">
            <div className="table-responsive">
              <div
                id="dataTable_wrapper"
                className="dataTables_wrapper dt-bootstrap4"
              >
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
                            Title
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
                            Description
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
                            Reason
                          </th>
                          <th
                            className="sorting_asc"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-sort="ascending"
                            aria-label="Name: activate to sort column descending"
                            style={{ width: 100 }}
                          >
                            Status
                          </th>
                            <th
                              className="sorting"
                              tabIndex={0}
                              aria-controls="dataTable"
                              rowSpan={1}
                              colSpan={1}
                              aria-label="Office: activate to sort column ascending"
                              style={{ width: 150 }}
                            >
                              Authorize
                            </th>
                       
                        </tr>
                      </thead>
                      <tbody>{dataShow}</tbody>
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

export default PostGuider;