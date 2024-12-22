### How to run the project
1. move to path `~\user-service` and run `docker build -t user-service -f docker-maven/Dockerfile .` to build the service image
2. move to path `~\user-service` and run `docker build -t user-sql-db -f docker-sql-db/Dockerfile .` to build the sql database image
2. move to path `~\user-service` and run `docker build -t user-mongo-db -f docker-mongo-db/Dockerfile .` to build the mongo database image
3. move to the project root path and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps

Alternative way for steps 1 and 2: move to path `~\user-service` and run this single command `docker build -t user-service -f docker-maven/Dockerfile . ; docker build -t user-sql-db -f docker-sql-db/Dockerfile . ; docker build -t user-mongo-db -f docker-mongo-db/Dockerfile .` to build all images at once