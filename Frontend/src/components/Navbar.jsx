import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <>
      {/* BARRA DE NAVEGACIÓN SUPERIOR */}
      <nav className="navbar navbar-expand navbar-dark bg-dark mb-4 shadow-sm">
        <div className="container">
          
          {/* Lado Izquierdo: Botón Menú + Logo */}
          <div className="d-flex align-items-center">
            {/* Botón que dispara el Offcanvas */}
            <button 
              className="btn btn-dark me-2 border-0" 
              type="button" 
              data-bs-toggle="offcanvas" 
              data-bs-target="#menuLateral"
            >
              <span className="fs-5">☰</span>
            </button>
            
            <Link className="navbar-brand m-0" to="/">
              ✈️ TravelAgency
            </Link>
          </div>
          
          {/* Lado Derecho: Mis Paquetes + Login */}
          <div className="d-flex align-items-center">
            <ul className="navbar-nav flex-row align-items-center">
              <li className="nav-item me-3">
                <Link className="nav-link px-2" to="/mis-paquetes">Mis paquetes</Link>
              </li>
              <li className="nav-item">
                <Link className="btn btn-outline-light d-flex align-items-center btn-sm py-2" to="/login">
                  <span className="me-2">👤</span> Iniciar sesión
                </Link>
              </li>
            </ul>
          </div>

        </div>
      </nav>

      {/* PANEL LATERAL DESLIZANTE (OFFCANVAS) */}
      <div className="offcanvas offcanvas-start" tabIndex="-1" id="menuLateral">
        
        <div className="offcanvas-header bg-dark text-white">
          <h5 className="offcanvas-title">🌍 Explorar</h5>
          <button type="button" className="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
        </div>
        
        <div className="offcanvas-body p-0">
          <div className="list-group list-group-flush mt-2">
            
            {/* Nota: data-bs-dismiss cierra el menú automáticamente al hacer clic */}
            <Link to="/paquetes" className="list-group-item list-group-item-action border-0 fs-5 py-3" data-bs-dismiss="offcanvas">
              🏖️ Todos los Paquetes
            </Link>
            <Link to="/ofertas" className="list-group-item list-group-item-action border-0 fs-5 py-3" data-bs-dismiss="offcanvas">
              🔥 Ofertas Especiales
            </Link>
            <Link to="/destinos" className="list-group-item list-group-item-action border-0 fs-5 py-3" data-bs-dismiss="offcanvas">
              🗺️ Destinos Populares
            </Link>
            <Link to="/nosotros" className="list-group-item list-group-item-action border-0 fs-5 py-3" data-bs-dismiss="offcanvas">
              🏢 Sobre Nosotros
            </Link>
            <Link to="/contacto" className="list-group-item list-group-item-action border-0 fs-5 py-3" data-bs-dismiss="offcanvas">
              📞 Contacto y Ayuda
            </Link>
            
          </div>
        </div>
      </div>
    </>
  );
};

export default Navbar;