# Build project in root dir
FROM maven:3.9.11-amazoncorretto-21 AS build

WORKDIR /build

COPY . .

RUN mvn clean package -DskipTests

# Use lighter image since we don't need maven anymore
FROM amazoncorretto:21

ARG PROJECT_NAME="lotlytics-api"
ARG PROJECT_VERSION="1.0.0"

# Downgrade privligies
RUN mkdir -p /home/lotlytics-api-agent && \
    echo "lotlytics-api-agent:x:1000:1000::/home/lotlytics-api-agent:/bin/bash" >> /etc/passwd && \
    echo "lotlytics-api-agent:x:1000:" >> /etc/group && \
    chown -R 1000:1000 /home/lotlytics-api-agent
USER lotlytics-api-agent
WORKDIR /home/lotlytics-api-agent

ENV SPRING_PROFILES_ACTIVE=container

# Copy build file from prior layer
COPY --from=build /build/target/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

CMD ["java", "-jar", "app.jar"]