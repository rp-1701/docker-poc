#pull google cloud cli image
FROM openjdk:11

WORKDIR /app

COPY ./docker-poc/output_jar/poc1.jar /app/poc1.jar

COPY ../key.json /app/key.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/key.json

CMD ["java", "-jar", "poc1.jar"]

