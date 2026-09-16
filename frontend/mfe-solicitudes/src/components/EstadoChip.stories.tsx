import type { Meta, StoryObj } from '@storybook/react';
import { EstadoChip } from './EstadoChip';

const meta: Meta<typeof EstadoChip> = {
  title: 'Componentes/EstadoChip',
  component: EstadoChip,
};
export default meta;

type Story = StoryObj<typeof EstadoChip>;

export const Registrada: Story = { args: { estado: 'REGISTRADA' } };
export const EnAtencion: Story = { args: { estado: 'EN_ATENCION' } };
export const Resuelta: Story = { args: { estado: 'RESUELTA' } };
export const Cerrada: Story = { args: { estado: 'CERRADA' } };
