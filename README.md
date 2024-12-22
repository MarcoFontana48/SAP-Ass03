### How to run the project
1. make sure to have Docker up and running on your machine
2. move to path `~\api-gateway` and run `docker build -t api-gateway -f docker-base-maven/Dockerfile .` to build the service image
3. move to path `~\configuration-server` and run `docker build -t config-server -f docker-maven/Dockerfile .` to build the service image
4. move to path `~\user-service` and run `docker build -t user-service -f docker-maven/Dockerfile . ; docker build -t user-sql-db -f docker-sql-db/Dockerfile . ; docker build -t user-mongo-db -f docker-mongo-db/Dockerfile .` to build the user-service and user-db images
5. move to path `~\bike-service` and run `docker build -t bike-service -f docker-maven/Dockerfile . ; docker build -t bike-sql-db -f docker-sql-db/Dockerfile . ; docker build -t bike-mongo-db -f docker-mongo-db/Dockerfile .` to build the bike-service and bike-db images
6. move to path `~\ride-service` and run `docker build -t ride-service -f docker-maven/Dockerfile . ; docker build -t ride-sql-db -f docker-sql-db/Dockerfile . ; docker build -t ride-mongo-db -f docker-mongo-db/Dockerfile .` to build the bike-service and bike-db images
7. move to the project root path `~\` (the parent directory of previous' steps paths) and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps
8. to run the clients: 
    - User client: move to path `~\user-ui\src\main\java\sap\ass02\infrastructure\UserClient.java` and run its main method
    - Admin client: move to path `~\admin-ui\src\main\java\sap\ass02\infrastructure\UserClient.java` and run its main method

### How to see the metrics
Open a browser and navigate to `http://localhost:9090` to see the metrics of the services

- execute query `user_service_requests_method_status_total` to see the number of requests made to the services divided by service type, method and status code 
- execute query `histogram_quantile(0.95, sum(rate(requests_latency_seconds_bucket[5m])) by (le))` to see the latency of the 95° (percentile) of requests made to the service in the last 5 minutes (remove the "[5m]" to see the latency of all requests)
- execute query `rate(requests_latency_seconds_sum[5m]) / rate(requests_latency_seconds_count[5m])` to see the average latency of requests made to the service in the last 5 minutes (remove the "[5m]" to see the average latency of all requests)
- execute query `requests_latency_seconds_bucket` to see the distribution of latency of requests made to the service in a histogram of bins of 'less than or equal to' the value specified in the output of the query "le=..." ("le" stands for "less or equal than" the number specified after the '=')

### Tests
To send test HTTP requests to the services, you can either run already available end-to-end tests on folders:
- `~\user-ui\src\test\java\sap\ass02\infrastructure\presentation\controller\property\StandardClientRequestTest.java`
- `~\admin-ui\src\test\java\sap\ass02\infrastructure\presentation\controller\property\StandardClientRequestTest.java`

Or test each service separately by running its tests on its test folder.

You can also manually send http requests using `curl` cmd, in both cases the api-gateway will automatically redirect the requests to the appropriate service based on path, here are some examples:
- add a user to user-service: `curl -X POST http://localhost:8080/app/user/ -H "Content-Type: application/json" -d '{"user_id": 1, "credit": 100}'`
- update a user to user-service: `curl -X PUT http://localhost:8080/app/user/ -H "Content-Type: application/json" -d '{"user_id": 1, "credit": 55}'`
- get a user from user-service: `curl -X GET http://localhost:8080/app/user/`
- add an ebike to bike-service: `curl -X POST http://localhost:8080/app/ebike/ -H "Content-Type: application/json" -d '{"ebike_id": 1, "state": "AVAILABLE", "x_location": 0, "y_location": 0, "x_direction": 1, "y_direction": 0, "speed": 0, "battery": 100}'`
- update an ebike to bike-service: `curl -X PUT http://localhost:8080/app/ebike/ -H "Content-Type: application/json" -d '{"ebike_id": 1, "state": "AVAILABLE", "x_location": 6, "y_location": 7, "x_direction": 8, "y_direction": 9, "speed": 5, "battery": 33}'`
- get an ebike from bike-service: `curl -X GET http://localhost:8080/app/ebike/`
- add a ride to ride-service: `curl -X POST http://localhost:8080/app/ride/ -H "Content-Type: application/json" -d '{"ride_id": 1, "user_id": 1, "ebike_id": 1, "action":"start"}'`
- stop a ride to ride-service: `curl -X PUT http://localhost:8080/app/ride/ -H "Content-Type: application/json" -d '{"ride_id": 1, "user_id": 1, "ebike_id": 1, "action":"stop"}'`
- get a ride from ride-service: `curl -X GET http://localhost:8080/app/ride/`
