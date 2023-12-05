FROM openjdk:20
RUN apt-get update && \
    apt-get install -y curl
RUN curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh
COPY test1-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
EXPOSE 2375
ENV DOCKER_HOST="tcp://host.docker.internal:2375"
ENTRYPOINT ["java", "-jar", "/app.jar"]
