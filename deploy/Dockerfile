# Use an OpenJDK base image
FROM openjdk:20-jdk-slim
LABEL authors="galya777"
# Set the working directory in the container
WORKDIR /app

# Copy application files
COPY .. /app

# Compile and build your game (if needed)
# RUN ./gradlew build (if you're using Gradle)
# For now, include any pre-built `.jar` file
COPY ../out/artifacts/DungeonsAndDragons_jar/DungeonsAndDragons.jar /app/

# Expose the port your game listens on
EXPOSE 8080

# Define the default command
CMD ["java", "-jar", "/app/app.jar"]

