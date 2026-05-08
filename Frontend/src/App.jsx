import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import PrivateRoute from './components/PrivateRoute';
import SyncUserSilently from './components/SyncUserSilently';
import Home from './views/Home';
import Paquetes from './views/Paquetes';
import DetallePaquete from './views/DetallePaquete';
import MiCuenta from './views/MiCuenta';
import SobreNosotros from './views/SobreNosotros';
import ContactoAyuda from './views/ContactoAyuda';
import Carrito from './views/Carrito';
import MisPaquetes from './views/MisPaquetes';
import AdminPanel from './views/AdminPanel';
import { CartProvider } from './context/CartContext';
import './App.css';

function App() {
  return (
    // Envolvemos todo con CartProvider para que el carrito sea accesible en cualquier ruta
    <CartProvider>
      <BrowserRouter>
        <Navbar />
        <SyncUserSilently />

        <div className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            
            <Route path="/paquetes" element={<Paquetes />} />
            <Route path="/paquete/:id" element={<DetallePaquete />} />
            <Route path="/mi-cuenta" element={<MiCuenta />} />
            <Route path="/nosotros" element={<SobreNosotros />} />
            <Route path="/contacto" element={<ContactoAyuda />} />
            
            {/* Ruta del Carrito: protegida para Clientes */}
            <Route path="/carrito" element={
              <PrivateRoute requiredRole="CLIENT">
                <Carrito />
              </PrivateRoute>
            } />

            
            
            <Route path="/mis-paquetes" element={
              <PrivateRoute requiredRole="CLIENT">
                <MisPaquetes />
              </PrivateRoute>
            } />

            <Route path="/admin" element={
              <PrivateRoute requiredRole="ADMIN">
                <AdminPanel />
              </PrivateRoute>
            } />
            
          </Routes>
        </div>
      </BrowserRouter>
    </CartProvider>
  );
}

export default App;