import { useState, useEffect } from "react";
import { useKeycloak } from "@react-keycloak/web";
import userService from "../services/user.service";

const MiCuenta = () => {
    const { keycloak, initialized } = useKeycloak();
    const [usuarioCompleto, setUsuarioCompleto] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [mensaje, setMensaje] = useState({ texto: "", tipo: "" });

    //Estado para los campos editables
    const [formData, setFormData] = useState({
        fullName: "",
        rut: "",
        nationality: "",
        phone: "",
        email: ""
    });

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
            // Extraemos el ID único de Keycloak desde el token
            const kId = keycloak.tokenParsed.sub; 
            cargarDatosUsuario(kId);
        }
    }, [initialized, keycloak]);

    const cargarDatosUsuario = (kId) => {
        userService.getByKeycloakId(kId)
            .then(response => {
                const data = response.data;
                setUsuarioCompleto(data);
                setFormData({
                    fullName: data.fullName || "",
                    rut: data.rut || "",
                    nationality: data.nationality || "",
                    phone: data.phone || "",
                    email: data.email || ""
                });
            })
            .catch(error => {
                console.error("Error al obtener datos del usuario:", error);
                setMensaje({ texto: "Error al cargar el perfil.", tipo: "danger" });
            });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleUpdate = (e) => {
        e.preventDefault();
        
        //Combinamos lo que no cambia (id, email, status) con lo nuevo
        const dataParaEnviar = {
            ...usuarioCompleto,
            fullName: formData.fullName,
            rut: formData.rut,
            nationality: formData.nationality,
            phone: formData.phone
        };

        userService.update(usuarioCompleto.id, dataParaEnviar)
            .then(response => {
                setUsuarioCompleto(response.data);
                setIsEditing(false);
                setMensaje({ texto: "¡Perfil actualizado correctamente!", tipo: "success" });
                setTimeout(() => setMensaje({ texto: "", tipo: "" }), 3000);
            })
            .catch(error => {
                console.error("Error al actualizar:", error);
                setMensaje({ texto: "No se pudo actualizar el perfil.", tipo: "danger" });
            });
    };

    if (!initialized || !usuarioCompleto) {
        return (
            <div className="container mt-5 text-center">
                <div className="spinner-border text-primary" role="status"></div>
                <p className="mt-2">Cargando tus datos...</p>
            </div>
        );
    }

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-7">
                    <div className="card shadow border-0">
                        <div className="card-header bg-dark text-white p-3">
                            <h4 className="mb-0">👤 Mi Perfil de Usuario</h4>
                        </div>
                        <div className="card-body p-4">
                            {mensaje.texto && (
                                <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>
                            )}

                            <form onSubmit={handleUpdate}>
                                <div className="mb-3">
                                    <label className="form-label fw-bold text-muted">Email (Identificador)</label>
                                    <input type="text" className="form-control bg-light" value={formData.email} disabled />
                                </div>

                                <div className="mb-3">
                                    <label className="form-label fw-bold">Nombre Completo</label>
                                    <input 
                                        type="text" 
                                        name="fullName"
                                        className={`form-control ${!isEditing ? 'bg-light' : ''}`}
                                        value={formData.fullName} 
                                        onChange={handleInputChange}
                                        disabled={!isEditing} 
                                        required
                                    />
                                </div>

                                <div className="row">
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold">RUT</label>
                                        <input 
                                            type="text" 
                                            name="rut"
                                            className={`form-control ${!isEditing ? 'bg-light' : ''}`}
                                            value={formData.rut} 
                                            onChange={handleInputChange}
                                            disabled={!isEditing} 
                                        />
                                    </div>
                                    <div className="col-md-6 mb-3">
                                        <label className="form-label fw-bold">Teléfono</label>
                                        <input 
                                            type="text" 
                                            name="phone"
                                            className={`form-control ${!isEditing ? 'bg-light' : ''}`}
                                            value={formData.phone} 
                                            onChange={handleInputChange}
                                            disabled={!isEditing} 
                                        />
                                    </div>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-bold">Nacionalidad</label>
                                    <input 
                                        type="text" 
                                        name="nationality"
                                        className={`form-control ${!isEditing ? 'bg-light' : ''}`}
                                        value={formData.nationality} 
                                        onChange={handleInputChange}
                                        disabled={!isEditing} 
                                    />
                                </div>

                                <div className="d-flex justify-content-end gap-2 border-top pt-3">
                                    {!isEditing ? (
                                        <button type="button" className="btn btn-primary" onClick={() => setIsEditing(true)}>
                                            Editar Datos
                                        </button>
                                    ) : (
                                        <>
                                            <button type="button" className="btn btn-secondary" onClick={() => setIsEditing(false)}>
                                                Cancelar
                                            </button>
                                            <button type="submit" className="btn btn-success">
                                                Guardar Cambios
                                            </button>
                                        </>
                                    )}
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MiCuenta;