import { useKeycloak } from "@react-keycloak/web";
import { Navigate } from "react-router-dom";

const PrivateRoute = ({ children, requiredRole }) => {
    const { keycloak, initialized } = useKeycloak();

    // Mientras Keycloak revisa la sesión
    if (!initialized) {
        return <div className="text-center mt-5"><h5>Verificando seguridad... 🛡️</h5></div>;
    }

    // Si no está autenticado, lo devolvemos a la página principal
    if (!keycloak.authenticated) {
        return <Navigate to="/" replace />;
    }

    // Si pedimos un rol específico (como ADMIN o CLIENT) y no lo tiene, también lo devolvemos
    if (requiredRole && !keycloak.hasRealmRole(requiredRole)) {
        console.warn("Acceso denegado: No tienes el rol necesario.");
        return <Navigate to="/" replace />;
    }

    // Si pasa todas las pruebas de seguridad, lo dejamos entrar a la ruta
    return children;
};

export default PrivateRoute;