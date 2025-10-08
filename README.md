# Welcome to the Micro Book Social Platform (Microservices) (MSP) repository

<p align="center">
  <img src="https://undraw.co/api/illustrations/undraw_Books_re_8gea.svg" alt="Micro Book Social Platform" width="400"/>
</p>

## Project Content
- **Description**: A book social platform built with a microservice architecture.
- **Architecture**: Microservices (Spring Boot, Docker, Kubernetes ready)
## Microsevices(until now)
- api-gateway(service)
- identify-service
- profile-service
- notification-service
- post-service
- file-service
- chat-service
- ...

---
# Deployment Guide: Micro Book Social Platform
This guide provides step-by-step instructions to containerize and deploy the entire microservices application using Docker and Docker Compose.
## Prerequisites
Before you begin, ensure you have the following installed on your system:
1.  **Docker**: [Get Docker](https://docs.docker.com/get-docker/)
2.  **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
3.  **Brevo API Key**: You need an API key from [Brevo](https://www.brevo.com/) for the notification service to work.
---
## Step 1: Create a `Dockerfile` for Each Microservice
For each Java-based service, you need a `Dockerfile` to build its container image. The content for each `Dockerfile` will be the same.
**`Dockerfile` Content:**
```dockerfile
FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```
Create a file named `Dockerfile` with the content above in the root directory of each of the following services:
- `api-gateway/`
- `chat-service/`
- `file-service/`
- `identity-service/`
- `notification-service/`
- `post-service/`
- `profile-service/`
---
## Step 2: Configure the `docker-compose.yml` File

The `docker-compose.yml` file orchestrates the startup and networking of all services, including the application microservices and their backing databases and message brokers.
Place the following content in the `docker-compose.yml` file in the project's root directory.
**Important:** Before running, find the line `BREVO_API_KEY=your_brevo_api_key` and replace `your_brevo_api_key` with your actual key.

---

## Step 3: Build and Run the Application

1.  Open a terminal in the root directory of the project (where your `docker-compose.yml` file is located).
2.  Run the following command to build the images for all services and start the containers in the background:

    ```bash
    docker-compose up --build -d
    ```

This command will:
- `--build`: Force the build of Docker images from the `Dockerfile` in each service directory.
- `-d`: Run the containers in detached mode, so they run in the background.

---

## Step 4: Accessing the Application

Once all containers are up and running, the entire platform is accessible through the API Gateway.

-   **API Gateway URL**: `http://localhost:8888`