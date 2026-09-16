import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { EstadoChip } from './EstadoChip';

describe('EstadoChip', () => {
  it('muestra el estado sin guión bajo', () => {
    render(<EstadoChip estado="EN_ATENCION" />);
    expect(screen.getByText('EN ATENCION')).toBeInTheDocument();
  });
});
