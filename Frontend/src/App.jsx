import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import SyncUserSilently from './components/SyncUserSilently';
import Paquetes from './views/Paquetes';
import DetallePaquete from './views/DetallePaquete';
import MiCuenta from './views/MiCuenta';
import SobreNosotros from './views/SobreNosotros';
import ContactoAyuda from './views/ContactoAyuda';
import Carrito from './views/Carrito';
import { CartProvider } from './context/CartContext'; // 1. Importamos el proveedor del carrito
import './App.css';

function App() {
  return (
    // 2. Envolvemos todo con CartProvider para que el carrito sea accesible en cualquier ruta
    <CartProvider>
      <BrowserRouter>
        <Navbar />
        <SyncUserSilently />

        <div className="container">
          <Routes>
            {/* Ruta de Inicio */}
            <Route path="/" element={
              <div className="text-center mt-5">
                <h1 className="text-primary">Bienvenido a TravelAgency</h1>
                <p>Explora y gestiona tus viajes de manera sencilla.</p>
              </div>
            } />
            
            <Route path="/paquetes" element={<Paquetes />} />
            <Route path="/paquete/:id" element={<DetallePaquete />} />
            <Route path="/mi-cuenta" element={<MiCuenta />} />
            <Route path="/nosotros" element={<SobreNosotros />} />
            <Route path="/contacto" element={<ContactoAyuda />} />
            
            {/* 3. Ruta del Carrito: protegida para Clientes */}
            <Route path="/carrito" element={
              <PrivateRoute requiredRole="CLIENT">
                <Carrito />
              </PrivateRoute>
            } />

            
            
            <Route path="/mis-paquetes" element={
              <div className="mt-4">
                <h3>📦 Mis Paquetes</h3>
                <p>En construcción...</p>
              </div>
            } />
            
          </Routes>
        </div>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;