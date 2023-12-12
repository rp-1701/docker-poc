#pull google cloud cli image
FROM openjdk:11

WORKDIR /app

COPY C:/Users/rohan_patil/IWC_poc/rajan_poc_code/poc1/target/poc1-0.0.1-SNAPSHOT.jar /app/poc1.jar

COPY ../key.json /app/key.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/key.json

CMD ["java", "-jar", "poc1.jar"]

