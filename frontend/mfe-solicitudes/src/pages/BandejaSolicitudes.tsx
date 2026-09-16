import { Alert, Box, CircularProgress, Table, TableBody, TableCell, TableHead, TableRow, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { apiFetch } from '../api/client';
import { EstadoChip } from '../components/EstadoChip';
import type { Solicitud } from '../schemas/solicitud';

type Estado = 'cargando' | 'listo' | 'vacio' | 'error';

/**
 * Bandeja de solicitudes con estados explícitos de carga/vacío/error.
 * Pendiente: filtros por estado/categoría, paginación completa y acciones por rol (ver README de frontend).
 */
export default function BandejaSolicitudes() {
  const [estado, setEstado] = useState<Estado>('cargando');
  const [solicitudes, setSolicitudes] = useState<Solicitud[]>([]);

  useEffect(() => {
    apiFetch<{ content: Solicitud[] }>('/solicitudes')
      .then((data) => {
        const items = data.content ?? [];
        setSolicitudes(items);
        setEstado(items.length === 0 ? 'vacio' : 'listo');
      })
      .catch(() => setEstado('error'));
  }, []);

  if (estado === 'cargando') {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress aria-label="Cargando solicitudes" />
      </Box>
    );
  }

  if (estado === 'error') {
    return <Alert severity="error">No fue posible cargar la bandeja de solicitudes.</Alert>;
  }

  if (estado === 'vacio') {
    return <Typography p={2}>No hay solicitudes registradas todavía.</Typography>;
  }

  return (
    <Table aria-label="Bandeja de solicitudes">
      <TableHead>
        <TableRow>
          <TableCell>Código</TableCell>
          <TableCell>Asunto</TableCell>
          <TableCell>Prioridad</TableCell>
          <TableCell>Estado</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {solicitudes.map((s) => (
          <TableRow key={s.id}>
            <TableCell>{s.codigo}</TableCell>
            <TableCell>{s.asunto}</TableCell>
            <TableCell>{s.prioridad}</TableCell>
            <TableCell><EstadoChip estado={s.estado} /></TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
