# Use an OpenJDK runtime image
FROM openjdk:21-slim


# Create a volume to store temporary files
VOLUME /tmp

# Copy the packaged jar file into the container
COPY target/ImageURLsProject-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
