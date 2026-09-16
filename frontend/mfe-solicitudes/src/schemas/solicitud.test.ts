import { describe, expect, it } from 'vitest';
import { CrearSolicitudSchema } from './solicitud';

describe('CrearSolicitudSchema', () => {
  it('rechaza asunto vacío', () => {
    const resultado = CrearSolicitudSchema.safeParse({
      asunto: '',
      descripcion: 'Descripción válida',
      categoriaId: '11111111-1111-1111-1111-111111111111',
      prioridad: 'MEDIA',
    });
    expect(resultado.success).toBe(false);
  });

  it('acepta un payload válido', () => {
    const resultado = CrearSolicitudSchema.safeParse({
      asunto: 'Falla en impresora',
      descripcion: 'La impresora del piso 3 no enciende',
      categoriaId: '11111111-1111-1111-1111-111111111111',
      prioridad: 'ALTA',
    });
    expect(resultado.success).toBe(true);
  });
});
