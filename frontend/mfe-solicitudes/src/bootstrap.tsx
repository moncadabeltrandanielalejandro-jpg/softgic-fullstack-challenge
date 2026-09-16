import { createRoot } from 'react-dom/client';
import BandejaSolicitudes from './pages/BandejaSolicitudes';

// Punto de entrada para ejecución standalone (fuera del shell federado).
const container = document.getElementById('root');
if (!container) {
  throw new Error('No se encontró el elemento #root');
}
createRoot(container).render(<BandejaSolicitudes />);
