### How to run the project 
1. make sure to have Docker up and running on your machine
2. move to root project path and run command `docker-compose up -d` to run the whole project and its services on containers based on images that are downloaded from docker hub
3. to run the clients:
   - Admin client: move to path `~\admin-ui\src\main\java\sap\ass02\infrastructure\AdminClient.java` and run its main method
   - User client: move to path `~\user-ui\src\main\java\sap\ass02\infrastructure\UserClient.java` and run its main method
4. when you're done using the application, run command `docker-compose down -v` to stop and remove the containers and volumes created by the project

### How to build and run the project
1. make sure to have Docker up and running on your machine
2. move to path `~\api-gateway` and run `docker build -t api-gateway -f docker-base-maven/Dockerfile .` to build the service image
3. move to path `~\configuration-server` and run `docker build -t config-server -f docker-maven/Dockerfile .` to build the service image
4. move to path `~\user-service` and run `docker build -t user-service -f docker-maven/Dockerfile . ; docker build -t user-sql-db -f docker-sql-db/Dockerfile . ; docker build -t user-mongo-db -f docker-mongo-db/Dockerfile .` to build the user-service and user-db images
5. move to path `~\bike-service` and run `docker build -t bike-service -f docker-maven/Dockerfile . ; docker build -t bike-sql-db -f docker-sql-db/Dockerfile . ; docker build -t bike-mongo-db -f docker-mongo-db/Dockerfile .` to build the bike-service and bike-db images
6. move to path `~\ride-service` and run `docker build -t ride-service -f docker-maven/Dockerfile . ; docker build -t ride-sql-db -f docker-sql-db/Dockerfile . ; docker build -t ride-mongo-db -f docker-mongo-db/Dockerfile .` to build the bike-service and bike-db images
7. move to the project root path `~\` (the parent directory of previous' steps paths) and run `docker-compose up -d` to run the whole project and its services on containers based on the images created in previous steps
8. to run the clients:
   - Admin client: move to path `~\admin-ui\src\main\java\sap\ass02\infrastructure\AdminClient.java` and run its main method
   - User client: move to path `~\user-ui\src\main\java\sap\ass02\infrastructure\UserClient.java` and run its main method

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

### Deployment on Kubernetes
To deploy the project on kubernetes, you need to have minikube installed on your machine or docker desktop with kubernetes enabled, then you can run the following commands:
- `minikube start` to start the minikube cluster or run 'docker desktop' with kubernetes enabled and run the next commands
- go to the root project path and run command: `kubectl apply -f kubernetes/` to deploy the project on the cluster
- apply port forwarding `kubectl port-forward svc/api-gateway 8080:8080`
- `kubectl get pods` to see the pods created (if you specified a different namespace than the 'default' one that is currently used by this project, use command '-n <namespace>')
- `kubectl describe pod <podname>` to see the details of a specific pod (if you specified a different namespace than the 'default' one that is currently used by this project, use command '-n <namespace>')
- `kubectl delete all --all` to delete all the resources created in the cluster (if you specified a different namespace than the 'default' one that is currently used by this project, use command '-n <namespace>')

### Testing the deployment on kubernetes
To test if api gateway can reach the services, run the following command:
- get the pod names: `kubectl get pods -A`
- run the following command: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -v http://ebike-service:8082/health`
- (example: `kubectl exec -it api-gateway-59898d76cd-xl4qg -- curl -v http://ebike-service:8082/health`)

example response from my machine:
```
$ kubectl exec -it api-gateway-59898d76cd-9fsdk -- curl -v http://ebike-service:8082/health
* Host ebike-service:8082 was resolved.
* IPv6: (none)
* IPv4: 10.99.99.152
*   Trying 10.99.99.152:8082...
* Connected to ebike-service (10.99.99.152) port 8082
> GET /health HTTP/1.1
> Host: ebike-service:8082
> User-Agent: curl/8.5.0
> Accept: */*
>
< HTTP/1.1 200 OK
< content-type: application/json
< content-length: 15
<
* Connection #0 to host ebike-service left intact
{"status":"ok"}
```
To test if the api gateway is reachable, run the following command:
- `curl -v http://localhost:8080/health`

example response from my machine:
```
$ curl -v http://localhost:8080/health
* Host localhost:8080 was resolved.
* IPv6: ::1
* IPv4: 127.0.0.1
*   Trying [::1]:8080...
* Connected to localhost (::1) port 8080
> GET /health HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/8.7.1
> Accept: */*
>
* Request completely sent off
  < HTTP/1.1 200 OK
  < content-type: application/json
  < content-length: 15
  <
  {"status":"ok"}* Connection #0 to host localhost left intact
```

You can also test sending requests from api-gateway to the services:
- get the api gateway pod name: `kubectl get pods -A`, find it under 'NAME' and paste it where API_GATEWAY_POD_NAME is written in the following commands:
- (example: `kubectl exec -it api-gateway-59898d76cd-9fsdk -- curl -X GET http://ebike-service:8082/app/ebike/`)
- add a user to user-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X POST http://user-service:8081/app/user/ -H "Content-Type: application/json" -d '{"user_id": 1, "credit": 100}'`
- update a user to user-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X PUT http://user-service:8081/app/user/ -H "Content-Type: application/json" -d '{"user_id": 1, "credit": 55}'`
- get a user from user-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X GET http://user-service:8081/app/user/`
- add an ebike to bike-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X POST http://ebike-service:8082/app/ebike/ -H "Content-Type: application/json" -d '{"ebike_id": 1, "state": "AVAILABLE", "x_location": 0, "y_location": 0, "x_direction": 1, "y_direction": 0, "speed": 0, "battery": 100}'`
- update an ebike to bike-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X PUT http://ebike-service:8082/app/ebike/ -H "Content-Type: application/json" -d '{"ebike_id": 1, "state": "AVAILABLE", "x_location": 6, "y_location": 7, "x_direction": 8, "y_direction": 9, "speed": 5, "battery": 33}'`
- get an ebike from bike-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X GET http://ebike-service:8082/app/ebike/`
- add a ride to ride-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X POST http://ride-service:8084/app/ride/ -H "Content-Type: application/json" -d '{"ride_id": 1, "user_id": 1, "ebike_id": 1, "action":"start"}'`
- stop a ride to ride-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X PUT http://ride-service:8084/app/ride/ -H "Content-Type: application/json" -d '{"ride_id": 1, "user_id": 1, "ebike_id": 1, "action":"stop"}'`
- get a ride from ride-service: `kubectl exec -it API_GATEWAY_POD_NAME -- curl -X GET http://ride-service:8084/app/ride/`

example response from my machine:
```
kubectl exec -it api-gateway-59898d76cd-9fsdk -- curl -X GET http://user-service:8081/app/user/
[{"user_id":"1","credit":100}]
```
