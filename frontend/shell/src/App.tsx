import { AppBar, Box, Button, CircularProgress, Container, Toolbar, Typography } from '@mui/material';
import { lazy, Suspense, useEffect, useState } from 'react';
import { Provider, useDispatch } from 'react-redux';
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom';
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
  if (!listo) return <Box minHeight="100vh" display="flex" alignItems="center" justifyContent="center"><CircularProgress aria-label="Inicializando sesión" /></Box>;

  return (
    <Box minHeight="100vh" sx={{ bgcolor: '#f5f7fb' }}>
      <AppBar position="static" elevation={0} sx={{ bgcolor: '#102a43' }}>
        <Toolbar sx={{ gap: 3 }}>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 700 }}>Softgic Solicitudes</Typography>
          <Button component={Link} to="/" color="inherit">Bandeja</Button>
          <Button component={Link} to="/analitica" color="inherit">Analítica</Button>
          <Typography variant="body2" sx={{ display: { xs: 'none', md: 'block' } }}>{keycloak.tokenParsed?.preferred_username}</Typography>
          <Button color="inherit" onClick={() => keycloak.logout({ redirectUri: window.location.origin })}>Salir</Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="xl" sx={{ py: 3 }}>
        <Suspense fallback={<Box display="flex" justifyContent="center" p={6}><CircularProgress aria-label="Cargando módulo" /></Box>}>
          <Routes>
            <Route path="/" element={<BandejaSolicitudes />} />
            <Route path="/analitica" element={<ResumenAnalitico />} />
          </Routes>
        </Suspense>
      </Container>
    </Box>
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
