import Config from '../Config';
import {wsConnect, wsDisconnect} from './webSocket';


export const logIn = json => ({ type: 'LOGIN', data: json });
export const exit = () => ({ type: 'LOGOUT' });
export const firErr = err => ({ type: 'ERROR', err });
export const visit = json => ({ type: 'VISIT', guider:json });


export const signIn = login => dispatch => fetch(`${Config.api_url}account/login`, {
      method: "POST",
      mode: "cors",
      credentials: "include",
      headers: {
            'Content-Type': 'application/json',

      },
      body: JSON.stringify(login)
})
      .then(res => res.json(), error => {
            console.log('An error occurred.', error);
            throw new Error(error);
            
      })
      .then((json) => {
            dispatch({ type: 'LOGIN', data: json });
            //dispatch(wsConnect(Config.api_url+"ws"));
            dispatch({type: 'ERROR', err: {msg: '', flag: false}});
      }).catch(err => {
            dispatch({type: 'ERROR', err: {msg: 'User name or password is wrong', flag: true}});
      });

export const logOut = () => dispatch => fetch(`${Config.api_url}account/logout`, {
      method: "GET",
      mode: "cors",
      credentials: "include",
})
      .then(res => res.json(), error => {
            console.log('An error occurred.', error);
            throw new Error(error);
      })
      .then(() => {
            dispatch(exit());
      }).catch(err => {
            dispatch({type: 'ERROR', err:  {msg: err, flag: true}});
      });



const gameInitialState = {
      userName: "",
      role: "",
      id: 0,
      logedIn: false,
      avatar: ""
};

const intialError = {
      msg: "nothing",
      flag: false
};
const initGuider = {
      guiderId: 0,
      guiderName: "",
      guiderAvatar: ""      
}




export const catchError = (state = { ...intialError }, action) => {
      switch (action.type) {
            case 'ERROR':
                  return {
                        msg: action.err.msg,
                        flag: !action.err.flag
                  };
            
            default:
                  return state;
      }
};

export const gameReducer = (state = { ...gameInitialState }, action) => {
      switch (action.type) {
            case 'LOGIN':
                  
                  return {
                        userName: action.data.userName,
                        role: action.data.role,
                        id: action.data.id,
                        logedIn: true,
                        avatar: ""
                  };
            case 'LOGOUT':
                        console.log("byebye");
                  return gameInitialState ;
            
            default:
                  return state;
      }
};