# Stage 1: Build the application
FROM --platform=linux/amd64 maven:3.8.3-openjdk-17 AS build
WORKDIR /easy-english-be

# Copy only the project definition files
COPY pom.xml .

# Download dependencies and cache them
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Set up the runtime environment
FROM --platform=linux/amd64 openjdk:17-jdk-slim AS runtime
WORKDIR /easy-english-be

# Copy the built JAR file from the previous stage
COPY --from=build /easy-english-be/target/easy-english-be.jar .

# Expose the port on which the application will run
EXPOSE 8001

ENTRYPOINT ["java", "-jar", "easy-english-be.jar"]