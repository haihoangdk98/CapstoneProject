import Config from '../Config';
import {wsConnect, wsDisconnect} from './webSocket';


export const logIn = json => ({ type: 'LOGIN', data: json });
export const exit = () => ({ type: 'LOGOUT' });
export const firErr = err => ({ type: 'ERROR', err:err });
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
            dispatch(wsConnect(Config.api_url+"ws"));
            dispatch(firErr({msg: '', flag: false}));
            
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
            dispatch(firErr({msg: '', flag: false}));
            
      }).catch(err => {
            dispatch(firErr({msg: '', flag: false}));
      });



const gameInitialState = {
      userName: "",
      role: "",
      id: 0,
      logedIn: false,
      avatar: ""
};

const intialError = {
      msg: "",
      flag: false
};
const initGuider = {
      guiderId: 0,
      guiderName: "",
      guiderAvatar: ""      
}




export const catchError = (state = { ...intialError }, action) => {
      //console.log(action);
      switch (action.type) {
            
            case 'ERROR':
                  return action.err;
            
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
                        avatar: "",
                        isGuiderActive:action.data.isGuiderActive,
                        isContractExist:action.data.isContractExist
                  };
            case 'LOGOUT':
                        console.log("byebye");
                  return gameInitialState ;
            
            default:
                  return state;
      }
};