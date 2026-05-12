# Usamos la imagen oficial de Nginx (ligera)
FROM nginx:alpine

# Copiamos la carpeta "dist" que generó el npm run build hacia la carpeta pública de Nginx
COPY dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponemos el puerto 80 que usa Nginx por defecto
EXPOSE 80

# Iniciamos Nginx
CMD ["nginx", "-g", "daemon off;"]