import Config from '../Config';


export const wsConnect = (host, name) => ({ type: 'WS_CONNECT', host, name });
export const wsConnecting = host => ({ type: 'WS_CONNECTING', host });
export const wsConnected = host => ({ type: 'WS_CONNECTED', host });
export const wsDisconnect = host => ({ type: 'WS_DISCONNECT', host });
export const wsDisconnected = host => ({ type: 'WS_DISCONNECTED', host });
export const send = message => ({ type: 'SEND', message });
export const get = () => ({ type: 'GET' });
export const save = message => ({ type: 'SAVE', message });
export const load = () => ({ type: 'LOAD' });
export const arrange = user => ({ type: 'ARRANGE', user });
export const clear = () => ({ type: 'CLEAR' });
export const announce = noti => ({ type: 'ANNOUNCE', noti });
const websocketInitialState = {};
const messageInitState = [];
const clients = [];
const notifications = []

export const websocketReducer = (state = { ...websocketInitialState }, action) => {
      switch (action.type) {
            case 'WS_CONNECTED':
                  console.log("test connect");
                  return { ...state, connected: true };
            case 'WS_DISCONNECTED':

                  clear();
                  return { state, connected: false };
            default:
                  return state;
      }
};

export const getMessages = (state = messageInitState, action) => {
      //console.log("save: ", action);
      switch (action.type) {
            case 'SAVE':

                  return [...state, action.message];
            case 'GET':

                  return state;
            default:
                  return state;
      }
};

export const arrangeClients = (state = clients, action) => {
      switch (action.type) {
            case 'ARRANGE':
                  let queue = Object.assign([], state);
                  if (queue.indexOf(action.user) >= 0) {
                        queue.splice(queue.indexOf(action.user), 1);
                        queue.unshift(action.user);
                  } else {
                        queue.unshift(action.user);
                  }
                  return queue;
            case 'GET':

                  return state;
            default:
                  return state;
      }
};

export const receiveNoti = (state = [...notifications], action) => {
      switch (action.type) {
            case 'ANNOUNCE':
                  return [...state, action.noti];
            default:
                  return state;
      }
}

export const loadGuest = guest => dispatch => fetch(`${Config.api_url}messages/${guest}`, {
      method: "GET",
      mode: "cors",
      credentials: "include",
      headers: {
            'Content-Type': 'application/json',
      },
      // body: JSON.stringify(login)
})

      .then(res => res.json(), error => {
            console.log('An error occurred.', error);
            throw new Error(error);
      })
      .then((json) => {
            json.array.forEach(element => {
                  dispatch({ type: 'ARRANGE', user: element });
            });
      }).catch(err => {
            dispatch({ type: 'ERROR', err: 'websocket error' });
      });

export const loadMsg = guest => dispatch => fetch(`${Config.api_url}messages/${guest}`, {
      method: "GET",
      mode: "cors",
      credentials: "include",
})
      .then(res => res.json(), error => {
            console.log('An error occurred.', error);
            throw new Error(error);
      })
      .then((json) => {
            json.array.forEach(element => {
                  dispatch({ type: 'SAVE', message: element });
            });
      }).catch(err => {
            dispatch({ type: 'ERROR', err: 'websocket error' });
      });