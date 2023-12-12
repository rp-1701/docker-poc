#pull google cloud cli image
FROM gcr.io/google.com/cloudsdktool/cloud-sdk:latest as gcloud

ENV PROJECT_ID=my-project-01-403308
ENV SERVICE_ACCOUNT_NAME=demo-service-account

RUN gcloud iam service-accounts keys create ./key.json --iam-account=$SERVICE_ACCOUNT_NAME@$PROJECT_ID.iam.gserviceaccount.com

FROM openjdk:11 as poc

WORKDIR /app

COPY --from=gcloud ./key.json /app/key.json

COPY /target/poc1-0.0.1-SNAPSHOT.jar /app/updated_poc1.jar

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/key.json

CMD ["java", "-jar", "updated_poc1.jar"]

