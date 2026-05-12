# Usamos una imagen oficial de Java 21 como sistema operativo base
FROM eclipse-temurin:21-jdk-alpine

# Etiqueta opcional para saber quién lo hizo
LABEL maintainer="sebastian.figueroa.re@usach.cl"

# Copiamos el .jar que acabamos de generar dentro del contenedor
COPY target/TravelAgency-Backend.jar app.jar

# Exponemos el puerto en el que corre tu Spring Boot
EXPOSE 8088

# El comando que se ejecutará cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "/app.jar"]