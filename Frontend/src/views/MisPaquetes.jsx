import { useEffect, useState } from "react";
import { useKeycloak } from "@react-keycloak/web";
import { Link } from "react-router-dom";
import reservationService from "../services/reservation.service";
import tourPackageService from "../services/tourPackage.service";
import userService from "../services/user.service";

const MisPaquetes = () => {
    const { keycloak, initialized } = useKeycloak();
    const [reservas, setReservas] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
            cargarHistorial();
        } else if (initialized && !keycloak.authenticated) {
            setLoading(false);
        }
    }, [initialized, keycloak]);

    const cargarHistorial = async () => {
        try {
            //Identificamos al usuario en nuestra BD
            const userRes = await userService.getByKeycloakId(keycloak.tokenParsed.sub);
            const userId = userRes.data.id;

            //Buscamos todas sus reservas
            const reservasRes = await reservationService.getByUserId(userId);
            const historial = reservasRes.data;

            //Buscamos el nombre de cada paquete para que se vea bonito
            const reservasEnriquecidas = await Promise.all(historial.map(async (reserva) => {
                try {
                    const paqueteRes = await tourPackageService.getById(reserva.tourPackageId);
                    return { 
                        ...reserva, 
                        packageName: paqueteRes.data.name,
                        destination: paqueteRes.data.destination
                    };
                } catch (error) {
                    return { ...reserva, packageName: "Paquete no disponible", destination: "N/A" };
                }
            }));

            //Ordenamos para que las compras más nuevas salgan primero
            reservasEnriquecidas.sort((a, b) => new Date(b.reservationDate) - new Date(a.reservationDate));
            
            setReservas(reservasEnriquecidas);
        } catch (error) {
            console.error("Error al cargar el historial de viajes:", error);
        } finally {
            setLoading(false);
        }
    };

    //Función para traducir y dar color al estado
    const renderBadgeEstado = (status) => {
        switch (status) {
            case "CONFIRMED":
                return <span className="badge bg-success px-3 py-2"><i className="bi bi-check-circle me-1"></i> Confirmado / Pagado</span>;
            case "PENDING_PAYMENT":
                return <span className="badge bg-warning text-dark px-3 py-2"><i className="bi bi-clock me-1"></i> Pendiente de Pago</span>;
            case "CANCELLED":
                return <span className="badge bg-danger px-3 py-2"><i className="bi bi-x-circle me-1"></i> Cancelado</span>;
            default:
                return <span className="badge bg-secondary">{status}</span>;
        }
    };

    if (loading) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status"></div>
                <p className="mt-3">Buscando tus aventuras...</p>
            </div>
        );
    }

    if (!keycloak.authenticated) {
        return (
            <div className="container mt-5 text-center">
                <h3>Debes iniciar sesión para ver tus viajes.</h3>
            </div>
        );
    }

    return (
        <div className="container mt-5 mb-5">
            <div className="d-flex justify-content-between align-items-end mb-4 border-bottom pb-3">
                <div>
                    <h2 className="fw-bold text-primary mb-0">📦 Mis Paquetes y Reservas</h2>
                    <p className="text-muted mt-1 mb-0">Historial de todas tus compras y comprobantes.</p>
                </div>
            </div>

            {reservas.length === 0 ? (
                <div className="text-center bg-light p-5 rounded shadow-sm">
                    <i className="bi bi-emoji-frown text-muted" style={{ fontSize: '4rem' }}></i>
                    <h4 className="mt-3">Aún no tienes viajes registrados</h4>
                    <p className="text-muted">¡Es un excelente momento para planear tus próximas vacaciones!</p>
                    <Link to="/paquetes" className="btn btn-primary mt-2">Ir al Catálogo</Link>
                </div>
            ) : (
                <div className="row">
                    {reservas.map((reserva) => (
                        <div className="col-12 mb-4" key={reserva.id}>
                            {/* Tarjeta estilo "Ticket" */}
                            <div className="card shadow-sm border-0 border-start border-5 border-primary h-100">
                                <div className="card-body p-4">
                                    <div className="row align-items-center">
                                        
                                        {/* Información Principal */}
                                        <div className="col-md-5 mb-3 mb-md-0 border-end">
                                            <h6 className="text-muted mb-1">CÓDIGO DE RESERVA: #{reserva.id}</h6>
                                            <h4 className="fw-bold text-dark mb-2">{reserva.packageName}</h4>
                                            <p className="mb-0 text-muted">
                                                <i className="bi bi-geo-alt-fill text-danger me-1"></i> Destino: {reserva.destination}
                                            </p>
                                        </div>

                                        {/* Detalles de la compra */}
                                        <div className="col-md-4 mb-3 mb-md-0 border-end px-md-4">
                                            <div className="mb-2">
                                                <strong>📅 Fecha de Compra:</strong> <br />
                                                {new Date(reserva.reservationDate).toLocaleDateString()} a las {new Date(reserva.reservationDate).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}
                                            </div>
                                            <div>
                                                <strong>👥 Pasajeros:</strong> {reserva.passengerQuantity} persona(s)
                                            </div>
                                        </div>

                                        {/* Estado y Monto */}
                                        <div className="col-md-3 text-md-end text-center">
                                            <div className="mb-3">
                                                {renderBadgeEstado(reserva.status)}
                                            </div>
                                            <h4 className="fw-bold text-success mb-1">${reserva.totalAmount}</h4>
                                            
                                            {/* Descuentos (Si los hubo) */}
                                            {reserva.appliedDiscounts && reserva.appliedDiscounts !== "Sin descuentos aplicables" && (
                                                <small className="text-info d-block mb-2" style={{ fontSize: '0.75rem' }}>
                                                    {reserva.appliedDiscounts}
                                                </small>
                                            )}

                                            <button 
                                                className="btn btn-outline-primary btn-sm w-100 mt-2"
                                                onClick={() => alert(`Generando PDF de comprobante para la reserva`)}
                                            >
                                                <i className="bi bi-receipt me-1"></i> Ver Comprobante
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MisPaquetes;