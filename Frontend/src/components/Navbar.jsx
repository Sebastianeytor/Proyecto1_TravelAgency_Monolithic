import { Link } from "react-router-dom";

const Navbar = () => {
  const isLoggedIn = false; 
  const isAdmin = false;    

  // Esta función simula un clic en el botón de la "X" para cerrar el menú
  // permitiendo que React Router haga su trabajo de navegación sin interrupciones.
  const cerrarMenu = () => {
    const btnCerrar = document.querySelector('.btn-close-white');
    if (btnCerrar) {
      btnCerrar.click();
    }
  };

  return (
    <>
      {/* BARRA SUPERIOR (Intacta) */}
      <nav className="navbar navbar-expand navbar-dark bg-dark mb-4 shadow-sm">
        <div className="container">
          <div className="d-flex align-items-center">
            <button className="btn btn-dark me-2 border-0" type="button" data-bs-toggle="offcanvas" data-bs-target="#menuLateral">
              <span className="fs-5">☰</span>
            </button>
            <Link className="navbar-brand m-0" to="/">✈️ TravelAgency</Link>
          </div>
          
          <div className="d-flex align-items-center">
            <ul className="navbar-nav flex-row align-items-center">
              <li className="nav-item me-3">
                <Link className="nav-link px-2" to="/carrito">🛒 Carrito</Link>
              </li>
              <li className="nav-item">
                {isLoggedIn ? (
                  <button className="btn btn-outline-danger d-flex align-items-center btn-sm py-2">
                    <span className="me-2">🚪</span> Cerrar sesión
                  </button>
                ) : (
                  <Link className="btn btn-outline-light d-flex align-items-center btn-sm py-2" to="/login">
                    <span className="me-2">👤</span> Iniciar sesión
                  </Link>
                )}
              </li>
            </ul>
          </div>
        </div>
      </nav>

      {/* MENÚ LATERAL */}
      <div className="offcanvas offcanvas-start" tabIndex="-1" id="menuLateral">
        <div className="offcanvas-header bg-dark text-white">
          <h5 className="offcanvas-title">🌍 Explorar</h5>
          {/* Este es el botón "X" que nuestra función buscará para cerrar el menú */}
          <button type="button" className="btn-close btn-close-white" data-bs-dismiss="offcanvas"></button>
        </div>
        
        <div className="offcanvas-body p-0 d-flex flex-column">
          <div className="list-group list-group-flush mt-2 flex-grow-1">
            
            {/* Quitamos el data-bs-dismiss y agregamos nuestro propio onClick */}
            <Link to="/paquetes" className="list-group-item list-group-item-action border-0 fs-5 py-3" onClick={cerrarMenu}>
              🏖️ Paquetes Turísticos
            </Link>
            <Link to="/mis-paquetes" className="list-group-item list-group-item-action border-0 fs-5 py-3" onClick={cerrarMenu}>
              📦 Mis Paquetes
            </Link>
            <Link to="/mi-cuenta" className="list-group-item list-group-item-action border-0 fs-5 py-3" onClick={cerrarMenu}>
              👤 Mi Cuenta
            </Link>
            <Link to="/nosotros" className="list-group-item list-group-item-action border-0 fs-5 py-3" onClick={cerrarMenu}>
              🏢 Sobre Nosotros
            </Link>
            <Link to="/contacto" className="list-group-item list-group-item-action border-0 fs-5 py-3" onClick={cerrarMenu}>
              📞 Contacto y Ayuda
            </Link>
            
          </div>

          {isAdmin && (
            <div className="p-3 bg-light border-top">
              <Link to="/admin" className="btn btn-warning w-100 fs-5 fw-bold" onClick={cerrarMenu}>
                ⚙️ Panel Administrador
              </Link>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Navbar;