import httpClient from "./http-common"; // Asegúrate de que la ruta sea correcta

// Obtener usuario por Keycloak ID (Ideal para cargar el perfil al iniciar sesión)
const getByKeycloakId = (keycloakId) => {
    return httpClient.get(`/users/keycloak/${keycloakId}`);
};

// Obtener usuario por ID interno (Alternativa)
const getById = (id) => {
    return httpClient.get(`/users/${id}`);
};

// Actualizar usuario
const update = (id, data) => {
    return httpClient.put(`/users/${id}`, data);
};

export default {
    getByKeycloakId,
    getById,
    update
};