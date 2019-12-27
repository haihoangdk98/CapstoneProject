import React, { Component } from 'react';
import CanvasJSReact from '../assets/canvasjs.react';
import Config from '../Config';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
var CanvasJSChart = CanvasJSReact.CanvasJSChart;
 
class ChartRevenue extends Component {
	constructor(props){
		super(props);
		this.state = {
			data:[],
			from:new Date(),
			to:new Date()
		}
	}
	async componentDidMount(){
		let {from,to} = this.state;
	    this.loadData(from,to);
	}

	loadData = async (from,to) => { 
		let fromDate = parseInt(from.getDate()) < 10 ? "0" + parseInt(from.getDate()) : parseInt(from.getDate());
		let fromMonth = parseInt(from.getMonth() + 1) < 10 ? "0" + parseInt(from.getMonth() + 1) : parseInt(from.getMonth() + 1);
		let fromYear = from.getFullYear();
		  // to date
		let toDate = parseInt(to.getDate()) < 10 ? "0" + parseInt(to.getDate()) : parseInt(to.getDate());
		let toMonth = parseInt(to.getMonth() + 1) < 10 ? "0" + parseInt(to.getMonth() + 1) : parseInt(to.getMonth() + 1);
		let toYear = to.getFullYear();
		let date = {
			from:fromMonth+'/'+fromDate+'/'+fromYear,
			to:toMonth+'/'+toDate+'/'+toYear,
		}
		let response = await fetch(Config.api_url + "statistic/totalRevenue",
					{
						method: "POST",
						mode: "cors",
						credentials: "include",
						headers: {
								'Content-Type': 'application/json'
						},
						body: JSON.stringify(date)
					}
			);
			if (!response.ok) { throw Error(response.status + ": " + response.statusText); }
			response = await response.json();
			let chartData = [];
			  for(let i = 0; i < response.length; i++) {
				let row = response[i];
				chartData.push({
					x: new Date(row.year, row.month), y: row.revenue
				});
			  }
			this.setState({data:chartData});
	  }
	
	  dateChange = async (date,message) => {
		let {from,to} = this.state;
		if(message === 'From'){
			if(date > to){
				from = to;
			}else{
				from = date ;
			}
		}
		else {
			if(date >= new Date()){
				to = new Date();
			}else if(date < from){
				to = new Date() ;
			}else{
				to = date;
			}
			
		}
		this.loadData(from,to);
		this.setState({from,to});
	  };
	  
	render() {
		let {data,from,to} = this.state;
		const options = {
			animationEnabled: true,
			title:{
				text: "Statistic of revenue by month - 2019"
			},
			axisX: {
				valueFormatString: "MMM"
			},
			axisY: {
				title: "Sales (in USD)",
				prefix: "$",
				includeZero: false
			},
			data: [{
				yValueFormatString: "$#,###",
				xValueFormatString: "MMMM",
				type: "spline",
				dataPoints: data
			}]
		}
		
		return (
		<div className="splineChart" style={{marginTop:'6%'}}>
			<h1>React Spline Chart</h1>
			<div className="selectDate">
				<span>From:{" "}</span><div className='fromDate'><DatePicker selected={from} onChange={(date)=>this.dateChange(date,'From')} /></div>
				<span>To:{" "}</span><div className='toDate'><DatePicker selected={to} onChange={(date)=>this.dateChange(date,'To')} /></div>
			</div>
			<CanvasJSChart options = {options} 

			/>
		</div>
		);
	}
}

export default ChartRevenue;       