### How to run the module
1. move to path `~\api-gateway` and either run `docker build -t api-gateway -f docker-spring-maven/Dockerfile .` or `docker build -t api-gateway -f docker-base-maven/Dockerfile .` to build the service image using the spring or the base java image
2. move to the project root path and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps
