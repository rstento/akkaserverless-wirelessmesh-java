FROM adoptopenjdk/openjdk8
ADD mycreds.json mycreds.json
ENV GOOGLE_APPLICATION_CREDENTIALS=mycreds.json