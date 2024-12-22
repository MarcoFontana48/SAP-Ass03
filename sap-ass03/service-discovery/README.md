### How to run the project
1. move to path `~\service-discovery` and run `docker build -t service-discovery -f docker-maven/Dockerfile .` to build the service image
2. move to the project root path and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps