# Stage 1 : Build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 : Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/target/docker-demo-0.0.1-SNAPSHOT.jar docker-demo.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/docker-demo.jar"]
