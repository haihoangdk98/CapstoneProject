import React, { Component } from "react";
import Config from "../Config";
import { Link } from "react-router-dom";

class Review extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data:[]
    };
  }

  async componentDidMount() {
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "review/reviewByPostIdAdmin?post_id="+this.props.match.params.post_id,
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    const data = await response.json();
    this.setState({data});
  };

  handleShowHide = async (param,index)=>{
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "review/showHideReview?trip_id="+param,
      autheticate
    );

    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    let {data} = this.state;
    data[index].visible = !data[index].visible;
    this.setState({data});
  }

  render() {
    let { data} = this.state;
    let dataShow = data.map((data, index) => (
      <tr role="row" className="odd" key={index}>
      <td className="sorting_1">{data.review}</td>
      { data.visible ?
        <td>
          <span className="btn btn-danger btn-icon-split triggerA" onClick={()=>{this.handleShowHide(data.trip_id,index)}}>
            <span className="icon text-white-50">
              <i className="fas fa-trash"></i>
            </span>
            <span className="text">Hide</span>
          </span>
        </td>
        : 
         <td>
          <span className="btn btn-primary btn-icon-split triggerA" onClick={()=>{this.handleShowHide(data.trip_id,index)}}>
            <span className="icon text-white-50">
              <i className="fas fa-flag"></i>
            </span>
            <span className="text">Show</span>
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
                            Review
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
                              Show/hide
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

export default Review;