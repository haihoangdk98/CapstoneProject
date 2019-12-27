import React, { Component } from 'react';
import CanvasJSReact from '../assets/canvasjs.react';
var CanvasJSChart = CanvasJSReact.CanvasJSChart;
 
class Chart extends Component {
	render() {
		const options = {
			animationEnabled: true,
			title:{
				text: "Monthly Sales - 2017"
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
				dataPoints: [
					{ x: new Date(2017, 0, 5), y: 25060 },
					{ x: new Date(2017, 0, 31), y: 250600 },
					{ x: new Date(2017, 1), y: 27980 },
					{ x: new Date(2017, 2), y: 42800 },
					{ x: new Date(2017, 3), y: 32400 },
					{ x: new Date(2017, 4), y: 35260 },
					{ x: new Date(2017, 5), y: 33900 },
					{ x: new Date(2017, 6), y: 40000 },
					{ x: new Date(2017, 7), y: 52500 },
					{ x: new Date(2017, 8), y: 32300 },
					
				]
			}]
		}
		
		return (
		<div className="splineChart" style={{marginTop:'6%'}}>
			<h1>Your income history</h1>
			<CanvasJSChart options = {options} 

			/>
		</div>
		);
	}
}

export default Chart;       