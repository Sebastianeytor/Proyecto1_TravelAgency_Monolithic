import axios from "axios";
import keycloak from "./keycloak"; // Importamos nuestro cerebro de seguridad

const httpClient = axios.create({
    baseURL: "http://localhost:8088/api", // Ojo: verifica que este sea el puerto correcto de tu Spring Boot
    headers: {
        "Content-type": "application/json"
    }
});

// EL INTERCEPTOR: Se ejecuta automáticamente ANTES de cada petición al backend
httpClient.interceptors.request.use(
    (config) => {
        // Si el usuario inició sesión y tiene un token, lo adjuntamos en la cabecera
        if (keycloak.authenticated && keycloak.token) {
            config.headers['Authorization'] = `Bearer ${keycloak.token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default httpClient;