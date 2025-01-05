# Use an OpenJDK base image
FROM openjdk:20-jdk-slim
LABEL authors="galya777"
# Set the working directory in the container
WORKDIR /app

# Copy application files
COPY .. /app


# For now, include any pre-built `.jar` file
COPY ./target/DungeonsAndDragons.jar /app/DungeonsAndDragons.jar

# Set the working directory inside the container
WORKDIR /app

# Expose the port your game listens on
EXPOSE 8080

# Define the default command
CMD ["java", "-jar", "/app/DungeonsAndDragons.jar"]

