# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

COPY wait-for-it.sh /wait-for-it.sh

# Copy the built jar file into the container
COPY target/*.jar app.jar

RUN chmod +x /wait-for-it.sh
CMD ["./wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "/app.jar"]

# Expose the port your app runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]