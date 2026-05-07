import { useEffect, useRef } from 'react';
import keycloak from '../services/keycloak';

const SyncUserSilently = () => {
    // Usamos un Ref para asegurarnos de no enviar el usuario 2 veces a la base de datos
    const hasSynced = useRef(false);

    useEffect(() => {
        const syncWithBackend = async () => {
            try {
                const email = keycloak.tokenParsed.email;
                const fullName = `${keycloak.tokenParsed.given_name || ''} ${keycloak.tokenParsed.family_name || ''}`.trim() || keycloak.tokenParsed.preferred_username;
                const keycloakId = keycloak.subject;

                const newUser = {
                    keycloakId: keycloakId,
                    email: email,
                    fullName: fullName
                };

                const response = await fetch('http://localhost:8088/api/users/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${keycloak.token}`
                    },
                    body: JSON.stringify(newUser)
                });

                if (response.ok) {
                    console.log("✅ Usuario sincronizado silenciosamente con la base de datos");
                } else if (response.status === 400) {
                    console.log("ℹ️ El usuario ya existía en la base de datos local");
                }
            } catch (error) {
                console.error("Error intentando sincronizar al usuario:", error);
            }
        };

        // El temporizador que revisa si Keycloak ya nos dio el Token
        const checkKeycloak = setInterval(() => {
            if (keycloak.authenticated && keycloak.tokenParsed && !hasSynced.current) {
                hasSynced.current = true; // Marcamos que ya hicimos el trabajo
                clearInterval(checkKeycloak); // Apagamos el reloj
                syncWithBackend(); // Disparamos el fetch
            }
        }, 500); // Revisa cada 500 milisegundos

        // Por seguridad, apagamos el reloj a los 10 segundos para no gastar memoria
        setTimeout(() => clearInterval(checkKeycloak), 10000);

        // Limpieza de React cuando el componente se desmonta
        return () => clearInterval(checkKeycloak);

    }, []); 

    return null; 
};

export default SyncUserSilently;