FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY springboot-backend/backend/.mvn/ .mvn
COPY springboot-backend/backend/mvnw .
COPY springboot-backend/backend/pom.xml .
RUN ./mvnw dependency:go-offline
COPY springboot-backend/backend/src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
