import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import tourPackageService from "../services/tourPackage.service";

const DetallePaquete = () => {
    const { id } = useParams();
    const [paquete, setPaquete] = useState(null);

    useEffect(() => {
        tourPackageService.getById(id)
            .then(response => {
                setPaquete(response.data);
            })
            .catch(error => {
                console.error("Error al cargar el detalle:", error);
            });
    }, [id]);

    if (!paquete) {
        return <div className="text-center mt-5"><h5>Cargando detalles del viaje... ⏳</h5></div>;
    }

    return (
        <div className="container mt-5 mb-5">
            <div className="row bg-white p-4 shadow-sm rounded">
                
                {/* COLUMNA IZQUIERDA: Imagen */}
                <div className="col-md-5 mb-4 mb-md-0">
                    <img 
                        src={paquete.imagen || "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=800&q=80"} 
                        alt={paquete.name} 
                        className="img-fluid rounded shadow w-100"
                        style={{ objectFit: 'cover', height: '100%', maxHeight: '500px' }}
                    />
                </div>

                {/* COLUMNA DERECHA: Información Completa */}
                <div className="col-md-7 d-flex flex-column">
                    <div className="d-flex justify-content-between align-items-start">
                        <h2 className="fw-bold mb-1">{paquete.name}</h2>
                        <h3 className="text-success fw-bold m-0">${paquete.price}</h3>
                    </div>
                    
                    {/* ETIQUETAS RÁPIDAS (Badges) */}
                    <div className="mt-2 mb-3">
                        <span className="badge bg-primary me-2 fs-6">⏱️ {paquete.duration}</span>
                        <span className="badge bg-info text-dark me-2 fs-6">🏕️ {paquete.tripType}</span>
                        <span className="badge bg-warning text-dark fs-6">☀️ {paquete.season}</span>
                    </div>

                    <p className="lead text-muted border-bottom pb-3">{paquete.description}</p>

                    {/* CUADRÍCULA DE DATOS DUROS */}
                    <div className="row bg-light p-3 rounded mb-3">
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">📍 Destino:</strong> <br/>
                            {paquete.destination}
                        </div>
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">🎟️ Cupos Disponibles:</strong> <br/>
                            <span className="text-danger fw-bold">{paquete.availableQuotas}</span>
                        </div>
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">🛫 Fecha Inicio:</strong> <br/>
                            {paquete.startDate}
                        </div>
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">🛬 Fecha Término:</strong> <br/>
                            {paquete.endDate}
                        </div>
                    </div>

                    {/* INFORMACIÓN DETALLADA (Textos largos) */}
                    <div className="mb-4 flex-grow-1">
                        <h6 className="fw-bold text-success">✅ Servicios Incluidos:</h6>
                        <p className="small text-muted">{paquete.includedServices}</p>

                        <h6 className="fw-bold text-warning">⚠️ Condiciones:</h6>
                        <p className="small text-muted">{paquete.conditions}</p>

                        <h6 className="fw-bold text-danger">🚫 Restricciones:</h6>
                        {/* Nota: Usamos la R mayúscula tal cual lo pediste */}
                        <p className="small text-muted">{paquete.Restrictions}</p>
                    </div>

                    {/* BOTONES DE ACCIÓN */}
                    <div className="d-grid gap-2 d-md-flex justify-content-md-end border-top pt-3">
                        <Link to="/paquetes" className="btn btn-outline-secondary px-4">
                            ⬅️ Volver
                        </Link>
                        <button 
                            className="btn btn-success px-5 fw-bold shadow-sm"
                            onClick={() => alert(`¡Agregaste "${paquete.name}" al carrito! 🛒`)}
                        >
                            🛒 Agregar al Carrito
                        </button>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default DetallePaquete;