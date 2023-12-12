#FROM openjdk:11

#WORKDIR /app

#COPY docker-poc/target/poc1-0.0.1-SNAPSHOT.jar /app/poc1.jar

#COPY key.json /app/key.json

#ENV GOOGLE_APPLICATION_CREDENTIALS=/app/key.json

#CMD ["java", "-jar", "poc1.jar"]

#############################################################

FROM gcr.io/google.com/cloudsdktool/cloud-sdk:latest

VOLUME [ "/credendials" ]

ENV PROJECT_ID=my-project-01-403308
ENV SERVICE_ACCOUNT_NAME=demo-service-account

RUN gcloud auth print-access-token --impersonate-service-account $SERVICE_ACCOUNT_NAME@$PROJECT_ID.iam.gserviceaccount.com > /credendials/key


FROM openjdk:11

WORKDIR /app

COPY docker-poc/target/poc1-0.0.1-SNAPSHOT.jar /app/poc1.jar

COPY key.json /app/key.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/key

CMD ["java", "-jar", "poc1.jar"]


