import { configureStore } from '@reduxjs/toolkit';
import sesionReducer from './features/sesion/sesionSlice';

export const store = configureStore({
  reducer: {
    sesion: sesionReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
