# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

# Execucao
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S zyclos && adduser -S zyclos -G zyclos
COPY --from=build /app/target/zyclos-api-*.jar app.jar
RUN mkdir -p /app/dados/uploads && chown -R zyclos:zyclos /app
USER zyclos
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV ZYCLOS_UPLOAD_DIR=/app/dados/uploads
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
