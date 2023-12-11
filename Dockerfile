#pull google cloud cli image
FROM gcr.io/google.com/cloudsdktool/cloud-sdk:latest

# authenticate and store the auth token
RUN gcloud auth login --quiet && \
    gcloud auth print-access-token --quiet > /token.txt

RUN /bin/sh -c 'gcloud auth print-access-/access_token'