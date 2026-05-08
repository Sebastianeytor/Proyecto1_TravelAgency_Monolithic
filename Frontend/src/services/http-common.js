import axios from "axios";
import keycloak from "./keycloak"; 

const httpClient = axios.create({
    baseURL: "http://localhost:8088/api", 
    headers: {
        "Content-type": "application/json"
    }
});

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