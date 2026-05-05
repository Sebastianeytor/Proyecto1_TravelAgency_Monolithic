import httpClient from "./http-common";

// Obtener todos los paquetes
const getAll = () => {
    return httpClient.get('/tour-packages'); 
}

// NUEVO: Obtener un paquete específico por su ID
const getById = (id) => {
    return httpClient.get(`/tour-packages/${id}`);
}

// Asegúrate de exportar ambas funciones
export default { getAll, getById };