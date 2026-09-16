import { Alert, Box, CircularProgress, Grid, Paper, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { apiFetch } from '../api/client';

interface ResumenIndicadores {
  porEstado: Array<{ estado: string; total: number }>;
  porCategoria: Array<{ categoriaId: string; total: number }>;
}

/** Vista analítica: conteos por estado, categoría y tendencia diaria (consume indicadores-service). */
export default function ResumenAnalitico() {
  const [datos, setDatos] = useState<ResumenIndicadores | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    apiFetch<ResumenIndicadores>('/indicadores/resumen')
      .then(setDatos)
      .catch(() => setError(true));
  }, []);

  if (error) return <Alert severity="error">No fue posible cargar el resumen analítico.</Alert>;
  if (!datos) return <Box display="flex" justifyContent="center" p={4}><CircularProgress /></Box>;

  return (
    <Grid container spacing={2} p={2}>
      <Grid item xs={12} md={6}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h6">Solicitudes por estado</Typography>
          {datos.porEstado.map((item) => (
            <Typography key={item.estado}>{item.estado}: {item.total}</Typography>
          ))}
        </Paper>
      </Grid>
      <Grid item xs={12} md={6}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h6">Solicitudes por categoría</Typography>
          {datos.porCategoria.map((item) => (
            <Typography key={item.categoriaId}>{item.categoriaId}: {item.total}</Typography>
          ))}
        </Paper>
      </Grid>
    </Grid>
  );
}
