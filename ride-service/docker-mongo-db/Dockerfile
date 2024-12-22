FROM mongo:latest

ENV MONGO_INITDB_ROOT_USERNAME=root
ENV MONGO_INITDB_ROOT_PASSWORD=password
ENV MONGO_INITDB_DATABASE=ebike

COPY ./init.js /docker-entrypoint-initdb.d/

EXPOSE 27017
