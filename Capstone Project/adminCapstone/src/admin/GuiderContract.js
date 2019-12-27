import React, { Component } from "react";
import Config from "../Config";

class GuiderContract extends Component {
  constructor(props) {
    super(props);
    this.state = {
        contract:[
        ],
        type:'Pending'
    };
  }

handleLoadData = async () =>{
  let autheticate = {
    method: "GET",
    mode: "cors",
    credentials: "include",
    headers: {
      Accept: "application/json"
    }
  };
  const response = await fetch(
    Config.api_url + "Guider/getAllContract",
    autheticate
  );

  if (!response.ok) {
    throw Error(response.status + ": " + response.statusText);
  }
  const contract = await response.json();
  this.setState({contract});
}

handleActiveContract = async ()=>{
  let autheticate = {
    method: "GET",
    mode: "cors",
    credentials: "include",
    headers: {
      Accept: "application/json"
    }
  };
  const response = await fetch(
    Config.api_url + "Guider/getActiveContract",
    autheticate
  );

  if (!response.ok) {
    throw Error(response.status + ": " + response.statusText);
  }
  const contract = await response.json();
  this.setState({contract});
}

async componentDidMount(){
  this.handleLoadData();
}

handleFillter = (type)=>{
  if(type === 'Pending'){
    this.handleLoadData();
  }else{
    this.handleActiveContract();
  }
  this.setState({type});
}

handleAccepct = async (guider_id,contract_id) =>{
  const r = window.confirm("Do you really want to accept contract??"); 
  if(r === true){
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "Guider/AcceptContract?guider_id="+guider_id+"&contract_id="+contract_id,
      autheticate
    );
  
    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    const acc = await response.json();
    this.handleLoadData();
  }
}

handleReject = async (guider_id,contract_id) =>{
  const r = window.confirm("Do you really want to reject contract??"); 
  if(r == true){ 
    let autheticate = {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
        Accept: "application/json"
      }
    };
    const response = await fetch(
      Config.api_url + "Guider/RejectContract?guider_id="+guider_id+"&contract_id="+contract_id,
      autheticate
    );
  
    if (!response.ok) {
      throw Error(response.status + ": " + response.statusText);
    }
    const acc = await response.json();
    this.handleLoadData();
   }
}

// handleDownLoad = async (contract_id) =>{

//   let api = Config.api_url + "Guider/DownloadDocument?contract_id="+contract_id;
//   window.open(api, 'blank');

// }

  render() {
    let {contract,type} = this.state;
    let dataAcc = contract.map((data, index) => {
      let gender ;
      if(data.gender === 1){
        gender =  'Male';
      }else if(data.gender === 2){
        gender =  'Female';
      }else{
        gender =  'Other';
      }
      return (
        type === 'Pending' ?
        <tr role="row" className="odd" key={index}>
        <td className="sorting_1">{data.name}</td>
        <td>{data.nationality}</td>
        <td>{data.date_of_birth.replace("00:00","")}</td>
        <td>{gender}</td>
        <td>{data.hometown}</td>
        <td>{data.address}</td>
        <td>{data.identity_card_number}</td>
        <td>{data.card_issued_date}</td>
        <td>{data.card_issued_province}</td>
        <td className="triggerA" style={{color:'#e71575'}} onClick={()=>{window.open(data.file_link, 'blank')}}>Download File</td>
        <td>
          <span className="btn btn-primary btn-icon-split triggerA" onClick={()=>{this.handleAccepct(data.guider_id,data.contract_id)}}>
            <span className="icon text-white-50">
              <i className="fas fa-flag"></i>
            </span>
            <span className="text">Accept</span>
          </span>
        </td>
        <td>
          <span className="btn btn-danger btn-icon-split triggerA" onClick={()=>{this.handleReject(data.guider_id,data.contract_id)}}>
            <span className="icon text-white-50">
              <i className="fas fa-trash"></i>
            </span>
            <span className="text">Reject</span>
          </span>
        </td>
      </tr>
      :
      <tr role="row" className="odd" key={index}>
        <td className="sorting_1">{data.name}</td>
        <td>{data.nationality}</td>
        <td>{data.date_of_birth.replace("00:00","")}</td>
        <td>{gender}</td>
        <td>{data.hometown}</td>
        <td>{data.address}</td>
        <td>{data.identity_card_number}</td>
        <td>{data.card_issued_date.split(' ')[0]}</td>
        <td>{data.card_issued_province}</td>
        <td className="triggerA" style={{color:'#e71575'}} onClick={()=>{window.open(data.file_link, 'blank')}}>Download File</td>
        </tr>
      )
    });
    return (
      <div className="container-fluid">
        {/* DataTales Example */}
        <div className="card shadow mb-4">
          <div className="card-header py-3">
            <h6 className="m-0 font-weight-bold text-primary">
              Manage Contract
            </h6>
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
                      onClick={() => this.handleFillter("Pending")}
                      className="btn btn-primary btn-icon-split traveler-button"
                    >
                      <span className="text">Pending</span>
                    </span>
                    <span
                      onClick={() => this.handleFillter("Active")}
                      className="btn btn-primary btn-icon-split guider-button"
                    >
                      <span className="text">Active</span>
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
                            Name
                          </th>
                          <th
                            className="sorting"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-label="Position: activate to sort column ascending"
                            style={{ width: 200 }}
                          >
                            Nationality
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
                            Date of birth
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
                            Gender
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
                            Home town
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
                            Address
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
                            Identity card number
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
                          Date of issue
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
                            Card issued province
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
                            Download file PDF
                          </th>
                          {
                            type === 'Pending' ?
                            <th
                            className="sorting"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-label="Office: activate to sort column ascending"
                            style={{ width: 150 }}
                          >
                            Accept
                          </th> : null
                          }
                          {
                            type === 'Pending' ?
                            <th
                            className="sorting"
                            tabIndex={0}
                            aria-controls="dataTable"
                            rowSpan={1}
                            colSpan={1}
                            aria-label="Age: activate to sort column ascending"
                            style={{ width: 150 }}
                          >
                            Reject
                          </th>:null
                          }
                        </tr>
                      </thead>
                      <tbody>
                       {dataAcc}
                      </tbody>
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

export default GuiderContract;
