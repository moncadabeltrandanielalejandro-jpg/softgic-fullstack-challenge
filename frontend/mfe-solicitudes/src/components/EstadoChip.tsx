import { Chip } from '@mui/material';

const COLORES: Record<string, 'default' | 'info' | 'warning' | 'success'> = {
  REGISTRADA: 'info',
  EN_ATENCION: 'warning',
  RESUELTA: 'success',
  CERRADA: 'default',
};

export interface EstadoChipProps {
  estado: 'REGISTRADA' | 'EN_ATENCION' | 'RESUELTA' | 'CERRADA';
}

/** Componente reutilizable documentado en Storybook (estados representativos). */
export function EstadoChip({ estado }: EstadoChipProps) {
  return <Chip label={estado.replace('_', ' ')} color={COLORES[estado]} size="small" />;
}
