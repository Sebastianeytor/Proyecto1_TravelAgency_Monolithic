import httpClient from "./http-common"; 

// 🔴 ADMIN: Trae todos los paquetes (activos, suspendidos, etc.)
const getAll = () => {
    return httpClient.get('/tour-packages'); 
}

// 🟢 CLIENTE/PÚBLICO: Trae solo los paquetes con estado "AVAILABLE"
const getAvailable = () => {
    return httpClient.get('/tour-packages/available');
};

// Búsqueda por destino directamente en la BD (Para un buscador global)
const searchByDestination = (destination) => {
    return httpClient.get(`/tour-packages/search?destination=${destination}`);
};

// Obtener un paquete específico por su ID
const getById = (id) => {
    return httpClient.get(`/tour-packages/${id}`);
}

export default { 
    getAll, 
    getAvailable, 
    searchByDestination, 
    getById 
};