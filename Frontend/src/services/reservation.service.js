import httpClient from "./http-common";

// 1. Obtener la cotización detallada (Épica 4)
const getPreview = (data) => {
    return httpClient.post("/reservations/preview", data);
};

// 2. Crear la reserva real (Descuenta cupos y guarda en BD)
const create = (data) => {
    return httpClient.post("/reservations", data);
};

// 3. Obtener el historial del usuario (Épica 6)
const getByUserId = (userId) => {
    return httpClient.get(`/reservations/user/${userId}`);
};

export default { getPreview, create, getByUserId };