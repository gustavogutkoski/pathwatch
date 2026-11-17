# Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw package -DskipTests -B

# Runtime
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]