Steps to setup and start the service:
1. Start docker on local machine
2. cd into the repository root folder
3. Run `docker-compose up --build` and wait for the service to start up. It will internally start elastic search as well

Steps to interact with the service:
1. For uploading a pdf:
    Endpoint: POST `/api/documents/upload`
    Request parameter: `file`
    Sample request: `curl -X POST -F "file=@pathToDocument.pdf" http://localhost:8080/api/documents/upload`
2. For searching:
   Endpoint: POST `/api/documents/search`
   Request parameter: `query`
   Sample request: `curl -X GET "http://localhost:8080/api/documents/search?query=term"`

Steps to run the test suite:
NOTE: 2 test pdf files are already included in the repository to run the tests efficiently
1. Start elasticsearch on docker using `docker compose up elasticsearch`
2. On a new terminal tab/window, run the following command to run all the tests `mvn test`