import { CircularProgress, Box } from '@mui/material';
import { lazy, Suspense, useEffect, useState } from 'react';
import { Provider, useDispatch } from 'react-redux';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { store } from './store';
import { initKeycloak, keycloak } from './keycloak';
import { sesionIniciada } from './features/sesion/sesionSlice';

// Carga federada del remoto; se muestra estado de carga explícito mientras resuelve.
const BandejaSolicitudes = lazy(() => import('mfeSolicitudes/BandejaSolicitudes'));
const ResumenAnalitico = lazy(() => import('mfeSolicitudes/ResumenAnalitico'));

function Rutas() {
  const dispatch = useDispatch();
  const [listo, setListo] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    initKeycloak()
      .then((autenticado) => {
        if (autenticado) {
          const roles = keycloak.tokenParsed?.realm_access?.roles ?? [];
          dispatch(sesionIniciada({ nombreUsuario: keycloak.tokenParsed?.preferred_username ?? '', roles }));
        }
        setListo(true);
      })
      .catch(() => setError('No fue posible inicializar la sesión con Keycloak'));
  }, [dispatch]);

  if (error) return <Box role="alert">{error}</Box>;
  if (!listo) return <Box display="flex" justifyContent="center" p={4}><CircularProgress /></Box>;

  return (
    <Suspense fallback={<Box display="flex" justifyContent="center" p={4}><CircularProgress /></Box>}>
      <Routes>
        <Route path="/" element={<BandejaSolicitudes />} />
        <Route path="/analitica" element={<ResumenAnalitico />} />
      </Routes>
    </Suspense>
  );
}

export default function App() {
  return (
    <Provider store={store}>
      <BrowserRouter>
        <Rutas />
      </BrowserRouter>
    </Provider>
  );
}
