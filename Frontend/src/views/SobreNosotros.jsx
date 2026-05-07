const SobreNosotros = () => {
    return (
        <div className="container mt-5 mb-5">
            {/* Cabecera / Hero Section */}
            <div className="text-center mb-5">
                <h1 className="display-4 fw-bold text-primary mb-3">Sobre Nosotros</h1>
                <p className="lead text-muted mx-auto" style={{ maxWidth: '700px' }}>
                    Somos tu puente hacia nuevas aventuras. Nos apasiona conectar a las personas con las culturas, paisajes y experiencias más increíbles del mundo.
                </p>
            </div>

            {/* Tarjetas de Misión y Visión */}
            <div className="row g-4 mb-5">
                <div className="col-md-6">
                    <div className="card h-100 shadow-sm border-0 bg-light hover-effect">
                        <div className="card-body text-center p-4">
                            <div className="mb-3">
                                <i className="bi bi-compass fs-1 text-success"></i>
                            </div>
                            <h3 className="card-title fw-bold">Nuestra Misión</h3>
                            <p className="card-text text-muted">
                                Brindar experiencias de viaje inolvidables y accesibles, garantizando un servicio transparente, seguro y de excelencia tecnológica desde el primer clic hasta el regreso a casa.
                            </p>
                        </div>
                    </div>
                </div>
                
                <div className="col-md-6">
                    <div className="card h-100 shadow-sm border-0 bg-light hover-effect">
                        <div className="card-body text-center p-4">
                            <div className="mb-3">
                                <i className="bi bi-eye fs-1 text-info"></i>
                            </div>
                            <h3 className="card-title fw-bold">Nuestra Visión</h3>
                            <p className="card-text text-muted">
                                Ser la agencia de viajes líder en innovación, transformando la manera en que los viajeros planifican y disfrutan sus rutas gracias a plataformas intuitivas y eficientes.
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Sección de "Por qué elegirnos" (Pilares) */}
            <div className="text-center mt-5 pt-4 border-top">
                <h3 className="fw-bold mb-5">¿Por qué viajar con nosotros?</h3>
                <div className="row g-4">
                    <div className="col-md-4">
                        <div className="p-3">
                            <i className="bi bi-shield-check display-4 text-primary mb-3 d-block"></i>
                            <h5 className="fw-bold">Seguridad Total</h5>
                            <p className="text-muted small">
                                Tus datos y reservas están protegidos con los más altos estándares de ciberseguridad.
                            </p>
                        </div>
                    </div>
                    
                    <div className="col-md-4">
                        <div className="p-3">
                            <i className="bi bi-geo-alt display-4 text-danger mb-3 d-block"></i>
                            <h5 className="fw-bold">Destinos Selectos</h5>
                            <p className="text-muted small">
                                Filtramos y diseñamos cada paquete para asegurarnos de ofrecerte solo lo mejor.
                            </p>
                        </div>
                    </div>
                    
                    <div className="col-md-4">
                        <div className="p-3">
                            <i className="bi bi-headset display-4 text-success mb-3 d-block"></i>
                            <h5 className="fw-bold">Soporte Continuo</h5>
                            <p className="text-muted small">
                                Nuestro sistema y equipo están listos para asistirte en cada paso de tu aventura.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SobreNosotros;