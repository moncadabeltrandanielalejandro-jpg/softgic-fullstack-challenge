import { Alert, Box, CircularProgress, Grid, Paper, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { indicadoresApiFetch } from '../api/client';

interface ResumenIndicadores {
  porEstado: Array<{ estado: string; total: number }>;
  porCategoria: Array<{ categoriaId: string; total: number }>;
}

interface DatoGrafica {
  etiqueta?: string | null;
  total: number;
}

function GraficaBarras({ datos, color }: { datos: DatoGrafica[]; color: string }) {
  const maximo = Math.max(...datos.map((item) => item.total), 1);

  return (
    <Box sx={{ mt: 2, display: 'grid', gap: 1.5 }}>
      {datos.map((item, indice) => {
        const etiqueta = item.etiqueta || 'Sin categoría';

        return (
        <Box key={`${etiqueta}-${indice}`}>
          <Box display="flex" justifyContent="space-between" gap={2}>
            <Typography variant="body2" noWrap title={etiqueta}>{etiqueta.replace('_', ' ')}</Typography>
            <Typography variant="body2" fontWeight={700}>{item.total}</Typography>
          </Box>
          <Box sx={{ mt: 0.5, height: 10, bgcolor: 'grey.200', borderRadius: 1, overflow: 'hidden' }}>
            <Box
              sx={{
                width: `${(item.total / maximo) * 100}%`,
                height: '100%',
                bgcolor: color,
                borderRadius: 1,
              }}
            />
          </Box>
        </Box>
        );
      })}
    </Box>
  );
}

/** Vista analítica: conteos por estado, categoría y tendencia diaria (consume indicadores-service). */
export default function ResumenAnalitico() {
  const [datos, setDatos] = useState<ResumenIndicadores | null>(null);
  const [error, setError] = useState(false);

  useEffect(() => {
    indicadoresApiFetch<ResumenIndicadores>('/indicadores/resumen')
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
          <GraficaBarras
            datos={datos.porEstado.map((item) => ({ etiqueta: item.estado, total: item.total }))}
            color="primary.main"
          />
        </Paper>
      </Grid>
      <Grid item xs={12} md={6}>
        <Paper sx={{ p: 2 }}>
          <Typography variant="h6">Solicitudes por categoría</Typography>
          <GraficaBarras
            datos={datos.porCategoria.map((item) => ({ etiqueta: item.categoriaId, total: item.total }))}
            color="secondary.main"
          />
        </Paper>
      </Grid>
    </Grid>
  );
}
