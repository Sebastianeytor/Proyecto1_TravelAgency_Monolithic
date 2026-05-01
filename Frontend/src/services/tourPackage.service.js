import httpClient from "./http-common";

// Obtener todos los paquetes
const getAll = () => {
    return httpClient.get('/tour-packages'); // Asegúrate de que coincida con el @RequestMapping de tu Controller en Java
}

// Aquí podrías agregar mas funciones como getById, etc.
export default { getAll };