# todo-springboot-demo

> Simple Todo-task Rest API using Spring Boot 2 and H2 embedded database. Project contains Unit and Integration tests.
## Requirements
* JDK8
* Maven
* Curl / Postman or similar for testing

## Running with Maven
Following example runs tests cases and starts application with embedded Tomcat on port 8080.
```sh
git clone https://github.com/kleht8/todo-springboot-demo.git
mvn clean install spring-boot:run
```
To run app without tests use
```mvn clean install -DskipTests spring-boot:run```


## Usage examples (curl)

### Create Todo Task
POST /api/v1/tasks
```
curl -X POST \
  http://localhost:8080/api/v1/tasks \
  -H 'Content-Type: application/json' \
  -d '{
	"name": "Task"
	"description": "Task description"
}'
```

### Get Todo Task
GET /api/v1/tasks/1
```
curl -X GET http://localhost:8080/api/v1/tasks/1 -H 'Content-Type: application/json'
```

### List and Filter Todo Tasks
```
curl -X GET http://localhost:8080/api/v1/tasks -H 'Content-Type: application/json'
curl -X GET http://localhost:8080/api/v1/tasks?search=name:Task -H 'Content-Type: application/json'
curl -X GET http://localhost:8080/api/v1/tasks?search=description:Task -H 'Content-Type: application/json'
curl -X GET http://localhost:8080/api/v1/tasks?search=done:false -H 'Content-Type: application/json'
```


### Update Todo Task
PUT /api/v1/tasks/1
```
curl -X PUT \
  http://localhost:8080/api/v1/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{
	"name": "Updated Task 1",
	"description": "Updated Description",
	"done": false
}'
```

### Update Todo Done status
PATCH /api/v1/tasks/1
```
curl -X PATCH \
  http://localhost:8080/api/v1/tasks/1 \
  -H 'Content-Type: application/json' \
  -d '{
	"done": true
}'
```

### Delete Todo Task
DELETE /api/v1/tasks/1
```
curl -X DELETE \
  http://localhost:8080/api/v1/tasks/1 \
  -H 'Content-Type: application/json'
```

