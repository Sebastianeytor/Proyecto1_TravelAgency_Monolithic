import axios from "axios";

// Esta es la configuración global para que React sepa dónde vive tu Spring Boot
export default axios.create({
  baseURL: "http://localhost:8088/api", // La ruta base de tu backend Monolítico
  headers: {
    "Content-type": "application/json"
  }
});