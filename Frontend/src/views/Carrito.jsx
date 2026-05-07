import { useContext, useState, useEffect } from "react";
import { CartContext } from "../context/CartContext";
import { useKeycloak } from "@react-keycloak/web";
import reservationService from "../services/reservation.service";
import userService from "../services/user.service";
import { useNavigate } from "react-router-dom";

const Carrito = () => {
    const { cart, removeFromCart, clearCart } = useContext(CartContext);
    const { keycloak } = useKeycloak();
    const navigate = useNavigate();

    // Estados
    const [dbUser, setDbUser] = useState(null);
    const [previews, setPreviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [step, setStep] = useState(1); // 1: Carrito, 2: Pago, 3: Éxito
    
    // Estado para el formulario de pago simulado
    const [paymentData, setPaymentData] = useState({ card: "", expiry: "", cvv: "" });

    // 1. Cargar el ID interno del usuario desde la BD
    useEffect(() => {
        if (keycloak.tokenParsed) {
            userService.getByKeycloakId(keycloak.tokenParsed.sub)
                .then(res => setDbUser(res.data))
                .catch(err => console.error("Error al identificar usuario", err));
        }
    }, [keycloak]);

    // 2. Obtener Cotizaciones (Previews) de Java apenas cambia el carrito
    useEffect(() => {
        if (dbUser && cart.length > 0) {
            obtenerCotizaciones();
        } else {
            setLoading(false);
        }
    }, [cart, dbUser]);

    const obtenerCotizaciones = async () => {
        setLoading(true);
        try {
            const peticiones = cart.map(item => 
                reservationService.getPreview({
                    userId: dbUser.id,
                    packageId: item.package.id,
                    passengers: item.passengers
                })
            );
            const respuestas = await Promise.all(peticiones);
            setPreviews(respuestas.map(r => r.data));
        } catch (err) {
            console.error("Error al cotizar", err);
        } finally {
            setLoading(false);
        }
    };

    // 3. Confirmar Reservas Reales (Épica 5)
    const handleFinalizarCompra = async (e) => {
        e.preventDefault();
        try {
            const reservas = cart.map(item => 
                reservationService.create({
                    userId: dbUser.id,
                    packageId: item.package.id,
                    passengers: item.passengers
                })
            );
            await Promise.all(reservas);
            clearCart();
            setStep(3); // Ir a pantalla de éxito
        } catch (err) {
            alert("Hubo un error al procesar tu reserva: " + err.response?.data);
        }
    };

    const totalFinal = previews.reduce((acc, curr) => acc + curr.finalAmount, 0);

    if (cart.length === 0 && step !== 3) {
        return (
            <div className="container mt-5 text-center">
                <h3>Tu carrito está vacío ✈️</h3>
                <p className="text-muted">¡Busca tu próximo destino en nuestro catálogo!</p>
                <button className="btn btn-primary" onClick={() => navigate("/paquetes")}>Ver Paquetes</button>
            </div>
        );
    }

    return (
        <div className="container mt-5 mb-5">
            {step === 1 && (
                <div className="row">
                    <div className="col-lg-8">
                        <h2 className="mb-4 fw-bold">🛒 Mi Carrito</h2>
                        {cart.map((item, index) => (
                            <div className="card shadow-sm mb-3 border-0" key={item.package.id}>
                                <div className="card-body">
                                    <div className="row align-items-center">
                                        <div className="col-md-2">
                                            <img src={item.package.imagen || "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=100"} className="img-fluid rounded" alt="trip" />
                                        </div>
                                        <div className="col-md-5">
                                            <h5 className="mb-1">{item.package.name}</h5>
                                            <p className="text-muted small mb-0">Pasajeros: {item.passengers}</p>
                                        </div>
                                        <div className="col-md-4 text-end">
                                            {previews[index] ? (
                                                <>
                                                    <span className="text-muted text-decoration-line-through small">${previews[index].basePrice}</span>
                                                    <h5 className="text-success mb-0">${previews[index].finalAmount}</h5>
                                                    <small className="text-info d-block">{previews[index].appliedDiscounts}</small>
                                                </>
                                            ) : <span>Cargando precio...</span>}
                                        </div>
                                        <div className="col-md-1">
                                            <button className="btn btn-outline-danger btn-sm" onClick={() => removeFromCart(item.package.id)}>×</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                    
                    <div className="col-lg-4">
                        <div className="card shadow-sm border-0 bg-light p-4">
                            <h4 className="fw-bold mb-4">Resumen</h4>
                            <div className="d-flex justify-content-between mb-3">
                                <span>Total a pagar:</span>
                                <h3 className="text-primary fw-bold">${totalFinal}</h3>
                            </div>
                            <button className="btn btn-primary w-100 fw-bold py-3" onClick={() => setStep(2)}>
                                Continuar al Pago
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {step === 2 && (
                <div className="row justify-content-center">
                    <div className="col-md-6">
                        <div className="card shadow border-0">
                            <div className="card-header bg-dark text-white p-3 text-center">
                                <h4 className="mb-0">💳 Pago Simulado</h4>
                            </div>
                            <div className="card-body p-4">
                                <p className="text-center text-muted small mb-4">Monto total a pagar: <strong>${totalFinal}</strong></p>
                                <form onSubmit={handleFinalizarCompra}>
                                    <div className="mb-3">
                                        <label className="form-label fw-bold">Número de Tarjeta</label>
                                        <input type="text" className="form-control" placeholder="XXXX XXXX XXXX XXXX" required />
                                    </div>
                                    <div className="row">
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label fw-bold">Expira (MM/YY)</label>
                                            <input type="text" className="form-control" placeholder="12/28" required />
                                        </div>
                                        <div className="col-md-6 mb-3">
                                            <label className="form-label fw-bold">CVV</label>
                                            <input type="text" className="form-control" placeholder="123" required />
                                        </div>
                                    </div>
                                    <div className="mt-4 d-flex gap-2">
                                        <button type="button" className="btn btn-outline-secondary w-50" onClick={() => setStep(1)}>Volver</button>
                                        <button type="submit" className="btn btn-success w-50 fw-bold">Confirmar Pago</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {step === 3 && (
                <div className="text-center mt-5 py-5">
                    <div className="mb-4">
                        <i className="bi bi-check-circle-fill text-success" style={{ fontSize: '5rem' }}></i>
                    </div>
                    <h1 className="fw-bold">¡Reserva Confirmada!</h1>
                    <p className="lead text-muted">Tu pago ha sido procesado exitosamente.</p>
                    <div className="mt-4">
                        <button className="btn btn-primary px-5" onClick={() => navigate("/paquetes")}>Seguir explorando</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Carrito;