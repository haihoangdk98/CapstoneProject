import React, { Component } from 'react';
import ReactDOM from 'react-dom';

import ReactDOMServer from 'react-dom/server';
class Image extends Component {
      constructor(props) {
            super(props);



      }
      componentDidMount() {
            const dom = ReactDOM.findDOMNode(this);
            if (dom instanceof HTMLElement) {
                  //query image
                  let im = dom.querySelectorAll(`li[id^="image"]`);
                  let i;
                  for (i = 0; i < im.length; i++) {
                  let im = dom.querySelectorAll(`li[id^="image"]`);
                        [i].style.display = "inline";
                  }
            } else {
                  console.log("find DOM do not work");
            }
      }



      render() {

            //console.log(this.props);
            let images = this.props.bases.map((base, index) =>
                  <li key={index} style={{ float: "left", display: "inline" }} id={`image${index}`}>
                        <img src={`${base}`} style={{ width: "192px", height: "108px" }} />
                        <button type="button" onClick={(eve) => {

                              eve.preventDefault();
                              this.props.deleteImg(index);

                        }} style={{borderRadius:'30px', cursor:'pointer ',border:' 1px solid',
                         width:'30px', height:'30px',justifyContent: 'center', alignItems: 'center'}} >X</button>
                  </li>);

            return (
                  <ul style={{ listStyleType: "none" }}>
                        {images}
                  </ul>
            );
      }
}

export default Image;