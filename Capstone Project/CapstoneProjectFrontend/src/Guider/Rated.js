import React from 'react';


class Rated extends React.Component {
      constructor(props) {
            super(props);

      }

      render() {
            let star = [];
            let i = this.props.number;
            while(i > 0) {
                  star.push(<i key={i} className="fa fa-star"></i>);
                  i--;  
            }
            return (
                  <div className="Rating">
                        {star}
                  </div>
            );
      }

}

export default Rated;