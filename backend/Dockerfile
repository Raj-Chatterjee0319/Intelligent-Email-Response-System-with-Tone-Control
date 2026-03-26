FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

# Install Maven
RUN apk add --no-cache maven

# Build project
RUN mvn clean package -DskipTests

# Run app
CMD ["java", "-jar", "target/*.jar"]
