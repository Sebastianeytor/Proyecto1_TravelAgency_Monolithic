import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Navbar />

      <div className="container">
        <Routes>
          <Route path="/" element={
            <div className="text-center mt-5">
              <h1 className="text-primary">Bienvenido a TravelAgency</h1>
              <p>Explora y gestiona tus viajes de manera sencilla.</p>
            </div>
          } />
          
          <Route path="/mis-paquetes" element={
            <div className="mt-4">
              <h3>📦 Mis Paquetes</h3>
              <p>Aquí se mostrará la lista de paquetes que has reservado...</p>
            </div>
          } />
          
          <Route path="/login" element={
            <div className="text-center mt-5">
              <h3>👤 Iniciar Sesión</h3>
              <p>Formulario en construcción...</p>
            </div>
          } />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;