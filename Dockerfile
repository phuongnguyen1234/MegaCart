# ---------- Build Frontend ----------
# Giai đoạn này đã khá tối ưu, chỉ thay npm install bằng npm ci để nhanh và ổn định hơn.
FROM node:20-alpine AS frontend
WORKDIR /app
COPY frontend/package*.json ./
# Sử dụng npm ci nhanh và đáng tin cậy hơn cho môi trường build tự động.
RUN npm ci
COPY frontend/ .
RUN npm run build

# ---------- Build Backend (Spring Boot) ----------
# Tối ưu giai đoạn này để cache lại các thư viện Maven.
FROM maven:3.9-eclipse-temurin-21-alpine AS backend
WORKDIR /app

# 1. Chỉ copy pom.xml để tải các dependency. Layer này sẽ được cache lại.
COPY backend/pom.xml .

# 2. Tải tất cả dependency. Bước này chỉ chạy lại khi pom.xml thay đổi.
RUN mvn dependency:go-offline

# 3. Copy mã nguồn và tài nguyên frontend đã build.
COPY backend/src ./src
COPY --from=frontend /app/dist ./src/main/resources/static

# 4. Build ứng dụng. Maven sẽ dùng dependency đã được cache.
# Cờ -T 1C cho phép build song song trên máy đa nhân.
RUN mvn -T 1C package -DskipTests

# ---------- Run ----------
# Sử dụng image JRE nhỏ gọn hơn thay vì JDK đầy đủ.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Asia/Ho_Chi_Minh", "-jar", "app.jar"]
