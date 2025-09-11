# ---------- Build Frontend ----------
FROM node:20-alpine AS frontend
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build

# ---------- Build Backend (Spring Boot) ----------
FROM maven:3.9.1-eclipse-temurin-21 AS backend
WORKDIR /app
COPY backend/pom.xml ./ 
COPY backend/src ./src
# Copy frontend build vào Spring Boot resources
COPY --from=frontend /app/dist ./src/main/resources/static
# Build backend jar
RUN mvn clean package -DskipTests

# ---------- Run ----------
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
