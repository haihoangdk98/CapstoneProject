import Config from '../Config';

export const newMessage = msg => ({ type: 'NEW_MESSAGE', msg });
export const updateGame = json => ({ type: 'SET_GAME', data: json });
export const leaveGame = () => ({ type: 'LEAVE_GAME' });
export const startRound = () => ({ type: 'START_ROUND' });
export const updateTimer = time => ({ type: 'UPDATE_TIMER', time });
export const makeMove = move => ({ type: 'MAKE_MOVE', move });
export const updateGamePlayer = player => ({ type: 'UPDATE_GAME_PLAYER', player });


export const getGame = id => dispatch => fetch(`${Config.api_url}/app/game/${id}`, {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
    
  },
})
  .then(res => res.json())
  .then((json) => {
    dispatch({ type: 'SET_GAME', data: json });
  });

const gameInitialState = { time: null };

export const gameReducer = (state = { ...gameInitialState }, action) => {
  switch (action.type) {
    case 'SET_GAME':
      return { ...state, game: action.data };
    case 'SHOW_GAMES':
      return { ...state, games: action.data };
    case 'UPDATE_TIMER':
      return { ...state, time: action.time };
    case 'UPDATE_GAME_PLAYER':
      return { ...state, gamePlayer: action.player };
    default:
      return state;
  }
};