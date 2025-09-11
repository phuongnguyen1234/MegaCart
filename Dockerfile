# ---------- Build Frontend ----------
FROM node:20-alpine AS frontend
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# ---------- Build Backend (Spring Boot) ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS backend
WORKDIR /app
COPY backend/ .
COPY --from=frontend /app/dist /app/src/main/resources/static
RUN mvn clean package -DskipTests

# ---------- Run ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
