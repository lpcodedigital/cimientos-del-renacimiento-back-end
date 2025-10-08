# ===============================
# 🏗️ Stage 1: Build (con Maven)
# ===============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos solo lo necesario para aprovechar cache en dependencias
COPY pom.xml .
#RUN mvn dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Compilamos y generamos el .jar
RUN mvn clean package -DskipTests


# ===============================
# 🚀 Stage 2: Production
# ===============================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el .jar generado desde el build stage
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto que usa Spring Boot
EXPOSE 8080

# Ejecutamos el jar
ENTRYPOINT ["java", "-jar", "app.jar"]