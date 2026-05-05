import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import Paquetes from './views/Paquetes';
import DetallePaquete from './views/DetallePaquete';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <div className="container">
        <Routes>
          {/* Ruta de Inicio */}
          <Route path="/" element={
            <div className="text-center mt-5">
              <h1 className="text-primary">Bienvenido a TravelAgency</h1>
              <p>Explora y gestiona tus viajes de manera sencilla.</p>
            </div>
          } />
          
          {/* 2. AQUÍ ESTÁ LA MAGIA: Conectamos la ruta /paquetes con tu archivo */}
          <Route path="/paquetes" element={<Paquetes />} />
          <Route path="/paquete/:id" element={<DetallePaquete />} />
          <Route path="/carrito" element={
            <PrivateRoute requiredRole="CLIENT">
              <div className="container mt-5">
                <h2>Mi Carrito de Compras 🛒</h2>
                <p>Esta es una zona súper secreta, solo para clientes logueados.</p>
              </div>
            </PrivateRoute>
          } />
          
          {/* Rutas de relleno para que el resto de botones del Navbar no den error al hacer clic */}
          <Route path="/mis-paquetes" element={<div className="mt-4"><h3>📦 Mis Paquetes</h3><p>En construcción...</p></div>} />
          <Route path="/carrito" element={<div className="mt-4"><h3>🛒 Carrito</h3><p>En construcción...</p></div>} />
          <Route path="/login" element={<div className="text-center mt-5"><h3>👤 Iniciar Sesión</h3><p>En construcción...</p></div>} />
          <Route path="/mi-cuenta" element={<div className="mt-4"><h3>👤 Mi Cuenta</h3><p>En construcción...</p></div>} />
          <Route path="/nosotros" element={<div className="mt-4"><h3>🏢 Sobre Nosotros</h3><p>En construcción...</p></div>} />
          <Route path="/contacto" element={<div className="mt-4"><h3>📞 Contacto y Ayuda</h3><p>En construcción...</p></div>} />
          
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;