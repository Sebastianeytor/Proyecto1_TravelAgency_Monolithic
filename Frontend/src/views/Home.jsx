import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div className="d-flex flex-column gap-5 mb-5">
            
            {/* Banner Principal */}
            <div 
                className="p-5 text-center bg-image rounded-4 shadow-lg position-relative overflow-hidden"
                style={{
                    backgroundImage: "url('https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=1200&q=80')",
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    height: '400px'
                }}
            >
                {/* Capa oscura para que el texto sea legible */}
                <div className="position-absolute top-0 start-0 w-100 h-100 bg-dark" style={{ opacity: 0.5 }}></div>
                
                <div className="position-relative d-flex flex-column justify-content-center align-items-center h-100 text-white z-index-1">
                    <h1 className="display-4 fw-bold mb-3 shadow-sm">Descubre el mundo a tu manera</h1>
                    <p className="lead mb-4 fw-medium">Explora destinos increíbles, gestiona tus reservas y vive experiencias inolvidables con TravelAgency.</p>
                    <Link to="/paquetes" className="btn btn-primary btn-lg fw-bold px-5 py-3 rounded-pill shadow">
                        ✈️ Explorar Destinos
                    </Link>
                </div>
            </div>

            {/* Tarjetas de información */}
            <div className="row g-4 mt-2">
                <div className="col-md-4">
                    <div className="card h-100 border-0 shadow-sm text-center p-4">
                        <div className="card-body">
                            <div className="mb-3">
                                <i className="bi bi-globe-americas text-primary" style={{ fontSize: '3rem' }}></i>
                            </div>
                            <h5 className="fw-bold">Destinos Exclusivos</h5>
                            <p className="text-muted">Contamos con convenios en los mejores destinos turísticos para garantizar una experiencia de primer nivel.</p>
                        </div>
                    </div>
                </div>

                <div className="col-md-4">
                    <div className="card h-100 border-0 shadow-sm text-center p-4">
                        <div className="card-body">
                            <div className="mb-3">
                                <i className="bi bi-shield-check text-success" style={{ fontSize: '3rem' }}></i>
                            </div>
                            <h5 className="fw-bold">Reservas Seguras</h5>
                            <p className="text-muted">Tu tranquilidad es lo primero. Nuestro sistema de reservas y pagos está protegido con los más altos estándares.</p>
                        </div>
                    </div>
                </div>

                <div className="col-md-4">
                    <div className="card h-100 border-0 shadow-sm text-center p-4">
                        <div className="card-body">
                            <div className="mb-3">
                                <i className="bi bi-headset text-info" style={{ fontSize: '3rem' }}></i>
                            </div>
                            <h5 className="fw-bold">Soporte 24/7</h5>
                            <p className="text-muted">Nuestro equipo de agentes de viaje está disponible en todo momento para resolver tus dudas antes y durante tu viaje.</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Call to Action */}
            <div className="bg-light rounded-4 p-5 text-center mt-4 border shadow-sm">
                <h3 className="fw-bold mb-3">¿Listo para tu próxima aventura?</h3>
                <p className="text-muted mb-4">Únete a cientos de viajeros que ya están planificando sus próximas vacaciones con nosotros.</p>
                <div className="d-flex justify-content-center gap-3">
                    <Link to="/nosotros" className="btn btn-outline-secondary px-4">Conoce más sobre nosotros</Link>
                    <Link to="/contacto" className="btn btn-outline-primary px-4">Contáctanos</Link>
                </div>
            </div>

        </div>
    );
};

export default Home;    