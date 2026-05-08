import httpClient from "./http-common";

//Obtener la cotización detallada
const getPreview = (data) => {
    return httpClient.post("/reservations/preview", data);
};

//Crear la reserva real
const create = (data) => {
    return httpClient.post("/reservations", data);
};

//Obtener el historial de reservas del usuario
const getByUserId = (userId) => {
    return httpClient.get(`/reservations/user/${userId}`);
};

//Calcula el costo total de todos los paquetes del carrito
const previewCart = (data) => {
    return httpClient.post("/reservations/cart/preview", data);
};

//Crea multiples reservas de lo que se encuentra en el carrito
const checkoutCart = (data) => {
    return httpClient.post("/reservations/cart/checkout", data);
};

//Ventas totales en un rango de fechas (Para el Admin)
const getSalesReport = (start, end) => {
    return httpClient.get(`/reservations/reports/sales?start=${start}&end=${end}`);
};

//Qué paquetes fueron los más vendidos en un rango de fechas (Para el Admin)
const getRankingReport = (start, end) => {
    return httpClient.get(`/reservations/reports/ranking?start=${start}&end=${end}`);
};

export default { getPreview, create, getByUserId, previewCart, checkoutCart, getSalesReport, getRankingReport };