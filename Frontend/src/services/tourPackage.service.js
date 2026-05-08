import httpClient from "./http-common"; 

//Muestra todos los paquetes
const getAll = () => {
    return httpClient.get('/tour-packages'); 
}

//Muestra solo los paquetes con estado "AVAILABLE"
const getAvailable = () => {
    return httpClient.get('/tour-packages/available');
};

//Búsqueda por destino directamente en la BD (Para un buscador global)
const searchByDestination = (destination) => {
    return httpClient.get(`/tour-packages/search?destination=${destination}`);
};

//Obtener un paquete específico por su ID
const getById = (id) => {
    return httpClient.get(`/tour-packages/${id}`);
}

//Crea un nuevo paquete (Para el Admin) 
const create = (data) => {
    return httpClient.post("/tour-packages", data);
};

//Actualiza el paquete (Para el Admin)
const update = (id, data) => {
    return httpClient.put(`/tour-packages/${id}`, data);
};

export default { 
    getAll, 
    getAvailable, 
    searchByDestination, 
    getById, 
    create, 
    update 
};