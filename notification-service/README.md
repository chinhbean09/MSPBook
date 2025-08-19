
Install Mongodb from Docker Hub
`docker pull bitnami/mongodb:7.0.11`
`docker run -d --name mongodb-7.0.11 -p 27017:27017 -e MONGODB_ROOT_USER=root -e MONGODB_ROOT_PASSWORD=root bitnami/mongodb:7.0.11`
