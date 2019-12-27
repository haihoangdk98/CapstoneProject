import { combineReducers } from 'redux';
import { gameReducer,catchError } from './actions';
import { websocketReducer, getMessages, arrangeClients, receiveNoti } from './webSocket'

const appReducer = combineReducers({
      websocket: websocketReducer,
      messages: getMessages,
      clients: arrangeClients,
      user: gameReducer,
      Error: catchError,
      notifications: receiveNoti 
});

const rootReducer = (state, action) => {
      // when a logout action is dispatched it will reset redux state
      if (action.type === 'LOGOUT') {
        state = undefined;
      }
    
      return appReducer(state, action);
    };


export default rootReducer;