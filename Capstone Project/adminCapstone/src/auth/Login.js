import React, { Component } from 'react';

class Login extends Component {
    constructor(props){
        super(props);
        this.state = {
            data:{
                userName:'',
                password:''
            },
            errors:{},
            user:{
                userName:'admin',
                password:'admin'
            }
        }
    }

    handleChange = (e)=>{
    const { data,errors } = this.state;
    const value = e.target.value;
    const name = e.target.name;
    if(value !== ''){
        errors['err'] ='';
    }
    data[name] = value;
    this.setState({ data });
    }

    isValidate = () => {
        const { data } = this.state;
        let isError = false;
        let errors = {};
        if(data.userName === '') {
          isError = true;
          errors['err'] = 'Please input username';
        }else if(data.password === '') {
          isError = true;
          errors['err'] = 'Please input password';
        }
    
        this.setState({ isError, errors });
        if(isError) 
          return true;
    
        return false;
      }

    handleSubmit = async (e)=>{
        e.preventDefault();
        let {errors} = this.state;
        if(this.isValidate()) {
            return false;
          }
          let {data, user} = this.state;
          if(data.userName === user.userName && data.password === user.password){
              localStorage.setItem('isAuthenticate', true);
              window.location.href = "/";
          }else{
            errors['err'] = 'Wrong password or username';
            this.setState({errors});
          }
    }

    render() {
        let {errors} = this.state;
        return (
            <div className="container">
            {/* Outer Row */}
            <div className="row justify-content-center">
              <div className="col-xl-10 col-lg-12 col-md-9" style={{marginTop: '7%'}}> 
                <div className="card o-hidden border-0 shadow-lg my-5">
                  <div className="card-body p-0">
                    {/* Nested Row within Card Body */}
                    <div className="row">
                      <div className="col-lg-6 d-none d-lg-block bg-login-image" />
                      <div className="col-lg-6">
                        <div className="p-5">
                          <div className="text-center">
                            <h1 className="h4 text-gray-900 mb-4">Welcome Back!</h1>
                          </div>
                          <form className="user">
                            <div className="form-group">
                              <input
                                type="text"
                                className="form-control form-control-user"
                                id="exampleInputEmail"
                                aria-describedby="emailHelp"
                                placeholder="User name"
                                name="userName"
                                onChange={(e)=>this.handleChange(e)}
                              />
                            </div>
                            <div className="form-group">
                              <input
                                type="password"
                                className="form-control form-control-user"
                                id="exampleInputPassword"
                                placeholder="Password"
                                name="password"
                                onChange={(e)=>this.handleChange(e)}
                              />
                            </div>
                            {errors['err'] ? <p style={{color: "red"}}>{errors['err']}</p> : ''}

                            <div className="form-group">
                              <div className="custom-control custom-checkbox small">
                                <input
                                  type="checkbox"
                                  className="custom-control-input"
                                  id="customCheck"
                                />
                                <label
                                  className="custom-control-label"
                                  htmlFor="customCheck"
                                >
                                  Remember Me
                                </label>
                              </div>
                            </div>
                            <button
                              className="btn btn-primary btn-user btn-block triggerA"
                              onClick={(e)=>this.handleSubmit(e)}
                            >
                              Login
                            </button>
                            <hr />
                           
                          </form>
                          
                        </div>
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

export default Login;