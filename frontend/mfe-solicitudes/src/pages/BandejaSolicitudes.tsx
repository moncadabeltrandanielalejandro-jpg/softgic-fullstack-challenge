import {
  Alert, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle,
  FormControl, InputLabel, MenuItem, Pagination, Paper, Select, Stack, Table, TableBody,
  TableCell, TableHead, TableRow, TextField, Typography,
} from '@mui/material';
import { useEffect, useState } from 'react';
import { apiFetch } from '../api/client';
import { EstadoChip } from '../components/EstadoChip';
import { CrearSolicitudSchema, type Categoria, type Solicitud } from '../schemas/solicitud';

type Estado = 'cargando' | 'listo' | 'vacio' | 'error';
type PageResponse = { content: Solicitud[]; totalPages: number; number: number };
type FormState = { asunto: string; descripcion: string; categoriaId: string; prioridad: 'BAJA' | 'MEDIA' | 'ALTA' };

const initialForm: FormState = { asunto: '', descripcion: '', categoriaId: '', prioridad: 'MEDIA' };

declare global {
  interface Window { __softgicRoles?: () => string[]; }
}

/**
 * Bandeja de solicitudes con estados explícitos de carga/vacío/error.
 * Pendiente: filtros por estado/categoría, paginación completa y acciones por rol (ver README de frontend).
 */
export default function BandejaSolicitudes() {
  const [estado, setEstado] = useState<Estado>('cargando');
  const [solicitudes, setSolicitudes] = useState<Solicitud[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(1);
  const [filtroEstado, setFiltroEstado] = useState('');
  const [formularioAbierto, setFormularioAbierto] = useState(false);
  const [detalle, setDetalle] = useState<Solicitud | null>(null);
  const [formulario, setFormulario] = useState<FormState>(initialForm);
  const [mensaje, setMensaje] = useState<string | null>(null);
  const roles = window.__softgicRoles?.() ?? [];
  const esAnalista = roles.includes('ANALISTA');
  const esSupervisor = roles.includes('SUPERVISOR');

  function cargar() {
    setEstado('cargando');
    const query = new URLSearchParams({ page: String(pagina), size: '8' });
    if (filtroEstado) query.set('estado', filtroEstado);
    Promise.all([
      apiFetch<PageResponse>(`/solicitudes?${query.toString()}`),
      apiFetch<Categoria[]>('/categorias'),
    ]).then(([data, cats]) => {
      setSolicitudes(data.content ?? []);
      setTotalPaginas(Math.max(data.totalPages ?? 1, 1));
      setCategorias(cats ?? []);
      setEstado((data.content ?? []).length === 0 ? 'vacio' : 'listo');
    }).catch(() => setEstado('error'));
  }

  useEffect(cargar, [pagina, filtroEstado]);

  async function crear() {
    const resultado = CrearSolicitudSchema.safeParse(formulario);
    if (!resultado.success) {
      setMensaje(resultado.error.issues[0]?.message ?? 'Revisa los datos');
      return;
    }
    try {
      await apiFetch('/solicitudes', { method: 'POST', body: JSON.stringify(resultado.data) });
      setFormulario(initialForm);
      setFormularioAbierto(false);
      setMensaje('Solicitud creada correctamente');
      cargar();
    } catch { setMensaje('No se pudo crear la solicitud'); }
  }

  async function ejecutar(path: string, body?: object) {
    try {
      await apiFetch(path, { method: 'POST', ...(body ? { body: JSON.stringify(body) } : {}) });
      setMensaje('Operación realizada correctamente');
      setDetalle(null);
      cargar();
    } catch { setMensaje('La operación no está permitida o falló'); }
  }

  const categoriaNombre = (id: string) => categorias.find((cat) => cat.id === id)?.nombre ?? id;

  if (estado === 'cargando') {
    return (
      <Box display="flex" justifyContent="center" p={4}>
        <CircularProgress aria-label="Cargando solicitudes" />
      </Box>
    );
  }

  return <Box sx={{ p: 3, maxWidth: 1200, mx: 'auto' }}>
    <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" gap={2} mb={3}>
      <Box><Typography variant="h4">Bandeja de solicitudes</Typography><Typography color="text.secondary">Gestiona el ciclo de vida operacional.</Typography></Box>
      <Button variant="contained" onClick={() => setFormularioAbierto(true)}>Nueva solicitud</Button>
    </Stack>
    {mensaje && <Alert onClose={() => setMensaje(null)} severity="info" sx={{ mb: 2 }}>{mensaje}</Alert>}
    <Paper sx={{ p: 2, mb: 2 }}><FormControl size="small" sx={{ minWidth: 220 }}><InputLabel>Filtrar estado</InputLabel><Select value={filtroEstado} label="Filtrar estado" onChange={(event) => { setPagina(0); setFiltroEstado(event.target.value); }}><MenuItem value="">Todos</MenuItem>{['REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'].map((value) => <MenuItem key={value} value={value}>{value}</MenuItem>)}</Select></FormControl></Paper>
    {estado === 'cargando' && <Box display="flex" justifyContent="center" p={5}><CircularProgress /></Box>}
    {estado === 'error' && <Alert severity="error">No fue posible cargar la bandeja. Verifica la sesión.</Alert>}
    {estado === 'vacio' && <Paper sx={{ p: 5, textAlign: 'center' }}><Typography>No hay solicitudes para este filtro.</Typography></Paper>}
    {estado === 'listo' && <Paper><Table aria-label="Bandeja de solicitudes"><TableHead><TableRow><TableCell>Código</TableCell><TableCell>Asunto</TableCell><TableCell>Categoría</TableCell><TableCell>Prioridad</TableCell><TableCell>Estado</TableCell><TableCell /></TableRow></TableHead><TableBody>{solicitudes.map((s) => <TableRow hover key={s.id}><TableCell>{s.codigo}</TableCell><TableCell>{s.asunto}</TableCell><TableCell>{categoriaNombre(s.categoriaId)}</TableCell><TableCell>{s.prioridad}</TableCell><TableCell><EstadoChip estado={s.estado} /></TableCell><TableCell><Button size="small" onClick={() => setDetalle(s)}>Ver</Button></TableCell></TableRow>)}</TableBody></Table></Paper>}
    <Stack alignItems="center" mt={2}><Pagination count={totalPaginas} page={pagina + 1} onChange={(_, value) => setPagina(value - 1)} /></Stack>

    <Dialog open={formularioAbierto} onClose={() => setFormularioAbierto(false)} fullWidth maxWidth="sm"><DialogTitle>Nueva solicitud</DialogTitle><DialogContent><Stack gap={2} mt={1}><TextField label="Asunto" value={formulario.asunto} onChange={(e) => setFormulario({ ...formulario, asunto: e.target.value })} /><TextField label="Descripción" multiline minRows={4} value={formulario.descripcion} onChange={(e) => setFormulario({ ...formulario, descripcion: e.target.value })} /><FormControl fullWidth><InputLabel>Categoría</InputLabel><Select value={formulario.categoriaId} label="Categoría" onChange={(e) => setFormulario({ ...formulario, categoriaId: e.target.value })}>{categorias.map((cat) => <MenuItem key={cat.id} value={cat.id}>{cat.nombre}</MenuItem>)}</Select></FormControl><FormControl fullWidth><InputLabel>Prioridad</InputLabel><Select value={formulario.prioridad} label="Prioridad" onChange={(e) => setFormulario({ ...formulario, prioridad: e.target.value as FormState['prioridad'] })}>{['BAJA', 'MEDIA', 'ALTA'].map((value) => <MenuItem key={value} value={value}>{value}</MenuItem>)}</Select></FormControl></Stack></DialogContent><DialogActions><Button onClick={() => setFormularioAbierto(false)}>Cancelar</Button><Button variant="contained" onClick={crear}>Crear</Button></DialogActions></Dialog>

    <Dialog open={Boolean(detalle)} onClose={() => setDetalle(null)} fullWidth maxWidth="sm"><DialogTitle>{detalle?.codigo} · {detalle?.asunto}</DialogTitle><DialogContent><Stack gap={2} mt={1}><Typography>{detalle?.descripcion}</Typography><Typography>Categoría: {detalle && categoriaNombre(detalle.categoriaId)}</Typography><EstadoChip estado={detalle?.estado ?? 'REGISTRADA'} />{detalle?.estado === 'REGISTRADA' && esAnalista && <Button variant="contained" onClick={() => ejecutar(`/solicitudes/${detalle.id}/asignaciones`)}>Tomar solicitud</Button>}{detalle?.estado === 'EN_ATENCION' && esAnalista && <TextField id="observacion-resolver" label="Observación de resolución" multiline minRows={3} />}{detalle?.estado === 'EN_ATENCION' && esAnalista && <Button variant="contained" onClick={() => ejecutar(`/solicitudes/${detalle.id}/transiciones/resolver`, { observacion: (document.getElementById('observacion-resolver') as HTMLInputElement)?.value ?? '' })}>Resolver</Button>}{detalle?.estado === 'RESUELTA' && esSupervisor && <Stack direction="row" gap={1}><Button onClick={() => ejecutar(`/solicitudes/${detalle.id}/transiciones/devolver`, { motivo: 'Devuelta desde la bandeja' })}>Devolver a atención</Button><Button variant="contained" onClick={() => ejecutar(`/solicitudes/${detalle.id}/transiciones/cerrar`, { motivo: 'Cierre desde la bandeja' })}>Cerrar</Button></Stack>}</Stack></DialogContent><DialogActions><Button onClick={() => setDetalle(null)}>Cerrar</Button></DialogActions></Dialog>
  </Box>;
}
