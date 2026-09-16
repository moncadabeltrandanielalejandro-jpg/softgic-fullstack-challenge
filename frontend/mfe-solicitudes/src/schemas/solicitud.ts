import { z } from 'zod';

export const PrioridadSchema = z.enum(['BAJA', 'MEDIA', 'ALTA']);
export const EstadoSchema = z.enum(['REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA']);

export const SolicitudSchema = z.object({
  id: z.string().uuid(),
  codigo: z.string(),
  asunto: z.string().min(1).max(200),
  descripcion: z.string().min(1).max(2000),
  categoriaId: z.string().uuid(),
  prioridad: PrioridadSchema,
  estado: EstadoSchema,
  solicitanteId: z.string().uuid(),
  analistaId: z.string().uuid().nullable(),
});

export type Solicitud = z.infer<typeof SolicitudSchema>;

export const CategoriaSchema = z.object({
  id: z.string().uuid(),
  nombre: z.string(),
  activa: z.boolean().optional(),
});

export type Categoria = z.infer<typeof CategoriaSchema>;

export const CrearSolicitudSchema = z.object({
  asunto: z.string().min(1, 'El asunto es obligatorio').max(200),
  descripcion: z.string().min(1, 'La descripción es obligatoria').max(2000),
  categoriaId: z.string().uuid('Selecciona una categoría válida'),
  prioridad: PrioridadSchema,
});

export type CrearSolicitudInput = z.infer<typeof CrearSolicitudSchema>;
