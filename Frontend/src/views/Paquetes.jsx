import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import tourPackageService from "../services/tourPackage.service";

const Paquetes = () => {
    // 1. Estado para guardar los paquetes que vienen de la BD
    const [paquetes, setPaquetes] = useState([]);

    // 2. Función que llama al servicio
    const init = () => {
        tourPackageService.getAll()
            .then(response => {
                console.log("Datos recibidos:", response.data);
                setPaquetes(response.data);
            })
            .catch(error => {
                console.log("Error al conectar con el Backend:", error);
            });
    };

    // 3. Se ejecuta automáticamente al abrir la página
    useEffect(() => {
        init();
    }, []);

    return (
        <div className="container mt-3">
            <h2 className="mb-4 text-center">🏖️ Nuestros Paquetes Turísticos</h2>
            
            <div className="row">
                {paquetes.length > 0 ? (
                    paquetes.map((p) => (
                        <div className="col-md-4 mb-4" key={p.id}>
                            <div className="card h-100 shadow-sm">
                                {/* Imagen de relleno si no tienes una en la BD */}
                                <img 
                                    src={p.imagen || "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=500&q=60"} 
                                    className="card-img-top" 
                                    alt="Paquete turístico" 
                                    style={{ height: '200px', objectFit: 'cover' }}
                                />
                                <div className="card-body d-flex flex-column">
                                    <h5 className="card-title">{p.name}</h5>
                                    <p className="card-text text-muted">{p.description}</p>
                                    <div className="mt-auto">
                                        <h4 className="text-success">${p.price}</h4>
                                        <Link to={`/paquete/${p.id}`} className="btn btn-primary w-100">Ver Detalles</Link>
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="text-center mt-5">
                        <p>No hay paquetes disponibles en este momento. 🚧</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Paquetes;