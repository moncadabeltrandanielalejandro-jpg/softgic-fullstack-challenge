import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

export interface SesionState {
  autenticado: boolean;
  nombreUsuario?: string;
  roles: string[];
}

const initialState: SesionState = {
  autenticado: false,
  roles: [],
};

const sesionSlice = createSlice({
  name: 'sesion',
  initialState,
  reducers: {
    sesionIniciada(state, action: PayloadAction<{ nombreUsuario: string; roles: string[] }>) {
      state.autenticado = true;
      state.nombreUsuario = action.payload.nombreUsuario;
      state.roles = action.payload.roles;
    },
    sesionCerrada(state) {
      state.autenticado = false;
      state.nombreUsuario = undefined;
      state.roles = [];
    },
  },
});

export const { sesionIniciada, sesionCerrada } = sesionSlice.actions;
export default sesionSlice.reducer;
