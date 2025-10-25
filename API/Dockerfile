# =============================
# Etapa 1: Build del jar con Maven
# =============================
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:resolve dependency:resolve-plugins

COPY src ./src
RUN mvn clean package -DskipTests

# =============================
# Etapa 2: Contenedor ligero con JDK
# =============================
FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar API.jar

# Argumento para el perfil (build-time)
ARG PROFILE=prod

# Lo guardo como variable de entorno en la imagen (runtime)
ENV SPRING_PROFILES_ACTIVE=$PROFILE

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "API.jar"]
