import { createContext, useState, useEffect } from "react";

// 1. Creamos el Contexto (El "almacén" global)
export const CartContext = createContext();

// 2. Creamos el Provider (El administrador que reparte los datos)
export const CartProvider = ({ children }) => {
    
    // Inicializamos el carrito buscando primero en el disco duro del navegador (localStorage)
    // Así, si el usuario presiona F5 o cierra la pestaña, no pierde sus viajes.
    const [cart, setCart] = useState(() => {
        const savedCart = localStorage.getItem("travelCart");
        return savedCart ? JSON.parse(savedCart) : [];
    });

    // Efecto de Persistencia: Cada vez que el estado 'cart' cambie, 
    // lo sobreescribimos automáticamente en el localStorage.
    useEffect(() => {
        localStorage.setItem("travelCart", JSON.stringify(cart));
    }, [cart]);

    // Función para agregar un paquete al carrito
    // Recibe el objeto del paquete completo y la cantidad de pasajeros
    const addToCart = (tourPackage, passengers) => {
        setCart((prevCart) => {
            // Verificamos si este viaje ya está en el carrito
            const existingItem = prevCart.find(item => item.package.id === tourPackage.id);

            if (existingItem) {
                // Si ya está, actualizamos la cantidad de pasajeros (por si se arrepintió y quiere llevar a más gente)
                return prevCart.map(item =>
                    item.package.id === tourPackage.id
                        ? { ...item, passengers: passengers }
                        : item
                );
            } else {
                // Si es un viaje nuevo, lo agregamos a la lista
                return [...prevCart, { package: tourPackage, passengers: passengers }];
            }
        });
    };

    // Función para eliminar un paquete específico usando su ID
    const removeFromCart = (packageId) => {
        setCart((prevCart) => prevCart.filter(item => item.package.id !== packageId));
    };

    // Función para vaciar el carrito (¡Vital para cuando el pago sea exitoso!)
    const clearCart = () => {
        setCart([]);
    };

    // Entregamos las variables y funciones a toda la aplicación
    return (
        <CartContext.Provider value={{ cart, addToCart, removeFromCart, clearCart }}>
            {children}
        </CartContext.Provider>
    );
};