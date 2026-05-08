import httpClient from "./http-common";

//Obtener usuario por Keycloak ID
const getByKeycloakId = (keycloakId) => {
    return httpClient.get(`/users/keycloak/${keycloakId}`);
};

//Obtener usuario por ID interno
const getById = (id) => {
    return httpClient.get(`/users/${id}`);
};

//Actualizar usuario
const update = (id, data) => {
    return httpClient.put(`/users/${id}`, data);
};

//Obtener todos los usuarios (Para el Admin)
const getAll = () => {
    return httpClient.get("/users");
};

//Desactivar usuario (Para el Admin)
const deactivate = (id) => {
    return httpClient.patch(`/users/${id}/deactivate`);
};

export default {
    getByKeycloakId,
    getById,
    update,
    getAll,
    deactivate
};