### How to run the project
1. move to path `~\bike-service` and run `docker build -t bike-service -f docker-maven/Dockerfile .` to build the service image
2. move to path `~\bike-service` and run `docker build -t bike-sql-db -f docker-sql-db/Dockerfile .` to build the database image
2. move to path `~\bike-service` and run `docker build -t bike-mongo-db -f docker-mongo-db/Dockerfile .` to build the database image
3. move to the project root path and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps

Alternative way for steps 1 and 2: move to path `~\bike-service` and run this single command `docker build -t bike-service -f docker-maven/Dockerfile . ; docker build -t bike-db -f docker-db/Dockerfile . ; docker build -t bike-mongo-db -f docker-mongo-db/Dockerfile .` to build all images at once