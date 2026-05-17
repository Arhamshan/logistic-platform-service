FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Step 1: Copy and install logistic-common-lib first
COPY logistic-common-lib/pom.xml ./logistic-common-lib/pom.xml
COPY logistic-common-lib/src ./logistic-common-lib/src
RUN mvn -f logistic-common-lib/pom.xml clean install -DskipTests

# Step 2: Build logistic-platform-service (logistic-common-lib is now in container's .m2)
COPY logistic-platform-service/pom.xml ./logistic-platform-service/pom.xml
COPY logistic-platform-service/src ./logistic-platform-service/src
RUN mvn -f logistic-platform-service/pom.xml clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/logistic-platform-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
