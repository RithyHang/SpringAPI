# Use official Java 21 image
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy project files
COPY . .

# Make gradlew executable
RUN chmod +x gradlew

# Build JAR, skip tests
RUN ./gradlew clean build -x test

# Expose default port
EXPOSE 8080

# Run the JAR
CMD ["java", "-jar", "build/libs/ProductAPI-0.0.1-SNAPSHOT.jar"]