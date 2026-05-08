import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import tourPackageService from "../services/tourPackage.service";

const Paquetes = () => {
    //Guarda los paquetes disponibles de la BD
    const [paquetes, setPaquetes] = useState([]);
    
    //Lo que realmente ve el usuario en pantalla
    const [paquetesFiltrados, setPaquetesFiltrados] = useState([]);

    //Estados para los filtros
    const [busqueda, setBusqueda] = useState("");
    const [tipoViaje, setTipoViaje] = useState(""); 
    const [orden, setOrden] = useState("");

    //Función que llama al servicio
    const init = () => {
        tourPackageService.getAvailable()
            .then(response => {
                console.log("Paquetes disponibles recibidos:", response.data);
                setPaquetes(response.data);
                setPaquetesFiltrados(response.data); 
            })
            .catch(error => {
                console.log("Error al conectar con el Backend:", error);
            });
    };

    //Se ejecuta automáticamente al abrir la página
    useEffect(() => {
        init();
    }, []);

    //Cada vez que cambie un filtro, se ejecuta esta función para actualizar lo que ve el usuario
    useEffect(() => {
        let resultado = [...paquetes];

        //Filtro por Texto
        if (busqueda) {
            resultado = resultado.filter(p => 
                (p.name && p.name.toLowerCase().includes(busqueda.toLowerCase())) || 
                (p.destination && p.destination.toLowerCase().includes(busqueda.toLowerCase()))
            );
        }

        //Filtro por Tipo de Viaje (tripType)
        if (tipoViaje) {
            resultado = resultado.filter(p => p.tripType === tipoViaje);
        }

        //Ordenamiento por Precio
        if (orden === "asc") {
            resultado.sort((a, b) => a.price - b.price);
        } else if (orden === "desc") {
            resultado.sort((a, b) => b.price - a.price);
        }

        //Actualizamos la vitrina
        setPaquetesFiltrados(resultado);
        
    }, [busqueda, tipoViaje, orden, paquetes]);

    return (
        <div className="container mt-4">
            <h2 className="mb-4 text-center">🏖️ Nuestros Paquetes Turísticos</h2>
            
            {/* PANEL DE FILTROS */}
            <div className="card shadow-sm mb-5 border-0 bg-light">
                <div className="card-body">
                    <div className="row g-3">
                        {/* Buscador */}
                        <div className="col-md-4">
                            <label className="form-label fw-bold">Buscar destino</label>
                            <input 
                                type="text" 
                                className="form-control" 
                                placeholder="Buscar..." 
                                value={busqueda}
                                onChange={(e) => setBusqueda(e.target.value)}
                            />
                        </div>

                        {/* Tipo de Viaje */}
                        <div className="col-md-4">
                            <label className="form-label fw-bold">Tipo de Viaje</label>
                            <select 
                                className="form-select" 
                                value={tipoViaje} 
                                onChange={(e) => setTipoViaje(e.target.value)}
                            >
                                <option value="">Todos</option>
                                <option value="Aventura">Aventura</option>
                                <option value="Familiar">Familiar</option>
                                <option value="Relajacion">Relajación</option>
                                <option value="Cultural">Cultural</option>
                                <option value="Naturaleza">Naturaleza</option>
                                <option value="Fiesta">Fiesta</option>
                            </select>
                        </div>

                        {/* Ordenar Por */}
                        <div className="col-md-4">
                            <label className="form-label fw-bold">Ordenar por</label>
                            <select 
                                className="form-select" 
                                value={orden} 
                                onChange={(e) => setOrden(e.target.value)}
                            >
                                <option value="">Relevancia</option>
                                <option value="asc">Menor precio primero</option>
                                <option value="desc">Mayor precio primero</option>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            {/* LISTA DE PAQUETES */}
            <div className="row">
                {paquetesFiltrados.length > 0 ? (
                    paquetesFiltrados.map((p) => (
                        <div className="col-md-4 mb-4" key={p.id}>
                            <div className="card h-100 shadow-sm">
                                <img 
                                    src={"https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=500&q=60"} 
                                    className="card-img-top" 
                                    alt={p.name} 
                                    style={{ height: '200px', objectFit: 'cover' }}
                                />
                                <div className="card-body d-flex flex-column">
                                    <div className="d-flex justify-content-between align-items-start mb-2">
                                        <h5 className="card-title fw-bold mb-0">{p.name}</h5>
                                        {p.duration && <span className="badge bg-info text-dark">{p.duration} días</span>}
                                    </div>
                                    
                                    <p className="text-primary mb-2">
                                        <i className="bi bi-geo-alt-fill me-1"></i> {p.destination}
                                    </p>

                                    <p className="card-text text-muted flex-grow-1">
                                        {p.description ? p.description.substring(0, 80) + '...' : 'Sin descripción disponible.'}
                                    </p>
                                    
                                    <div className="mt-auto pt-3 border-top">
                                        <div className="d-flex justify-content-between align-items-center mb-3">
                                            <h4 className="text-success fw-bold mb-0">${p.price}</h4>
                                            <span className="badge bg-secondary">{p.tripType}</span>
                                        </div>
                                        <Link to={`/paquete/${p.id}`} className="btn btn-primary w-100 fw-bold">
                                            Ver Detalles
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="col-12 text-center mt-5 mb-5">
                        <h4 className="text-muted">No encontramos paquetes con esos filtros. ✈️</h4>
                        <button className="btn btn-outline-secondary mt-3" onClick={() => {
                            setBusqueda(""); setTipoViaje(""); setOrden("");
                        }}>
                            Limpiar Filtros
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Paquetes;