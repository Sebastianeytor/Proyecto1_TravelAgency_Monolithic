import { useEffect, useState, useContext } from "react";
import { useParams, Link } from "react-router-dom";
import tourPackageService from "../services/tourPackage.service";
import { CartContext } from "../context/CartContext"; 

const DetallePaquete = () => {
    const { id } = useParams();
    const [paquete, setPaquete] = useState(null);
    
    //Estados para el carrito y mensajes
    const { addToCart } = useContext(CartContext);
    const [passengers, setPassengers] = useState(1); 
    const [mensajeExito, setMensajeExito] = useState(false);
    const [mensajeError, setMensajeError] = useState(""); // NUEVO: Estado para el error

    useEffect(() => {
        tourPackageService.getById(id)
            .then(response => {
                setPaquete(response.data);
            })
            .catch(error => {
                console.error("Error al cargar el detalle:", error);
            });
    }, [id]);

    //Función mejorada con alertas de error
    const handleAgregarCarrito = () => {
        //Validar que no sea menor o igual a 0
        if (passengers <= 0) {
            setMensajeError("Debes seleccionar al menos 1 pasajero.");
            setTimeout(() => setMensajeError(""), 3500);
            return;
        }

        //Validar que no supere el límite de la base de datos
        if (passengers > paquete.availableQuotas) {
            setMensajeError(`¡Ups! Solo quedan ${paquete.availableQuotas} cupos disponibles para este paquete.`);
            setTimeout(() => setMensajeError(""), 3500);
            return;
        }

        //Si pasa las validaciones, agregamos al carrito
        setMensajeError("");
        addToCart(paquete, passengers);
        setMensajeExito(true);
        
        setTimeout(() => {
            setMensajeExito(false);
        }, 3000);
    };

    if (!paquete) {
        return <div className="text-center mt-5"><h5>Cargando detalles del viaje... ⏳</h5></div>;
    }

    const isSoldOut = paquete.availableQuotas <= 0;

    return (
        <div className="container mt-5 mb-5">
            
            {/* ALERTAS FLOTANTES */}
            {mensajeExito && (
                <div className="alert alert-success shadow-sm mb-4" role="alert">
                    <i className="bi bi-check-circle-fill me-2"></i>
                    ¡Se agregaron <strong>{passengers}</strong> cupo(s) para "{paquete.name}" a tu carrito!
                </div>
            )}

            {mensajeError && ( // NUEVO: Panel de error rojo
                <div className="alert alert-danger shadow-sm mb-4" role="alert">
                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                    {mensajeError}
                </div>
            )}

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
                    
                    <div className="mt-2 mb-3">
                        <span className="badge bg-primary me-2 fs-6">⏱️ {paquete.duration}</span>
                        <span className="badge bg-info text-dark me-2 fs-6">🏕️ {paquete.tripType}</span>
                        <span className="badge bg-warning text-dark fs-6">☀️ {paquete.season}</span>
                    </div>

                    <p className="lead text-muted border-bottom pb-3">{paquete.description}</p>

                    <div className="row bg-light p-3 rounded mb-3">
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">📍 Destino:</strong> <br/>
                            {paquete.destination}
                        </div>
                        <div className="col-sm-6 mb-2">
                            <strong className="text-dark">🎟️ Cupos Disponibles:</strong> <br/>
                            {isSoldOut ? (
                                <span className="badge bg-danger">AGOTADO</span>
                            ) : (
                                <span className="text-danger fw-bold">{paquete.availableQuotas}</span>
                            )}
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

                    <div className="mb-4 flex-grow-1">
                        <h6 className="fw-bold text-success">✅ Servicios Incluidos:</h6>
                        <p className="small text-muted">{paquete.includedServices}</p>

                        <h6 className="fw-bold text-warning">⚠️ Condiciones:</h6>
                        <p className="small text-muted">{paquete.conditions}</p>

                        <h6 className="fw-bold text-danger">🚫 Restricciones:</h6>
                        <p className="small text-muted">{paquete.Restrictions}</p>
                    </div>

                    {/* CONTROLES DE COMPRA Y BOTONES */}
                    <div className="d-flex flex-wrap justify-content-between align-items-center border-top pt-3">
                        <Link to="/paquetes" className="btn btn-outline-secondary px-4 mb-2">
                            ⬅️ Volver
                        </Link>
                        
                        <div className="d-flex align-items-center mb-2">
                            <label className="fw-bold me-2 text-dark">Pasajeros:</label>
                            <input 
                                type="number" 
                                className="form-control text-center me-3" 
                                style={{ width: '80px' }}
                                min="1" 
                                max={paquete.availableQuotas}
                                value={passengers}
                                onChange={(e) => setPassengers(Number(e.target.value))}
                                disabled={isSoldOut}
                            />
                            
                            <button 
                                className="btn btn-success px-4 fw-bold shadow-sm"
                                onClick={handleAgregarCarrito}
                                disabled={isSoldOut}
                            >
                                {isSoldOut ? "Agotado" : "🛒 Agregar al Carrito"}
                            </button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default DetallePaquete;