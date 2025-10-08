# Architecture Overview: Micro Book Social Platform

This document explains the architecture of the Micro Book Social Platform, how the services interact, and the role of each component.

## Core Concept

The application is built on a **microservices architecture**. Each core feature (like identity, profiles, posts) is a separate, independently deployable service. This design promotes scalability, resilience, and maintainability.

**Docker Compose** is used to orchestrate all these services and their dependencies for a consistent development and deployment environment.

---

## Service Breakdown

Here is a description of each service and its responsibilities:

### 1. API Gateway (`api-gateway`)
-   **Port**: `8888`
-   **Description**: This is the single entry point for all client requests (from the web or mobile app). It routes incoming requests to the appropriate microservice. It is also responsible for cross-cutting concerns like authentication checks and rate limiting.

### 2. Identity Service (`identity-service`)
-   **Port**: `8080`
-   **Database**: MySQL (`mysql-identity`)
-   **Description**: Manages user authentication and authorization. It handles user registration, login, and issues JSON Web Tokens (JWTs) to authenticated users. All other services that require user authentication will validate the JWT provided.

### 3. Profile Service (`profile-service`)
-   **Port**: `8081`
-   **Database**: Neo4j (`neo4j-profile`)
-   **Description**: Manages user profiles, relationships, and social graphs (e.g., who follows whom). Neo4j is used here because a graph database is highly efficient for managing complex relationships.

### 4. Post Service (`post-service`)
-   **Port**: `8083`
-   **Database**: MongoDB (`mongo-db`)
-   **Description**: Handles the creation, retrieval, and management of user posts, comments, and likes. MongoDB is chosen for its flexible schema, which is ideal for storing varied post content.

### 5. File Service (`file-service`)
-   **Port**: `8084`
-   **Database**: MongoDB (`mongo-db`)
-   **Storage**: Docker Volume (`file-storage`)
-   **Description**: Manages file uploads, storage, and retrieval, such as profile pictures or images in posts. It stores file metadata in MongoDB and the actual files in a persistent Docker volume.

### 6. Chat Service (`chat-service`)
-   **Port**: `8085`
-   **Database**: MongoDB (`mongo-db`)
-   **Description**: Powers real-time private and group messaging. It likely uses WebSockets for persistent connections with clients and MongoDB to store chat history.

### 7. Notification Service (`notification-service`)
-   **Port**: `8082`
-   **Database**: MongoDB (`mongo-db`)
-   **Message Broker**: Kafka
-   **Description**: Manages and sends notifications to users (e.g., via email or push notifications). It listens for events from other services (like a new follow or a new post mention) published on Kafka topics and acts on them. It uses an external service (Brevo) to send emails.

---

## Data and Infrastructure

-   **MySQL (`mysql-identity`)**: A relational database used by `identity-service` for storing user credentials and roles.
-   **Neo4j (`neo4j-profile`)**: A graph database perfect for the `profile-service` to manage the social network graph.
-   **MongoDB (`mongo-db`)**: A single MongoDB instance that hosts separate databases for the `post-service`, `chat-service`, `file-service`, and `notification-service`. This is efficient for services that benefit from a flexible, document-based data model.
-   **Kafka**: A distributed event streaming platform. It decouples services by allowing them to communicate asynchronously. For example, the `post-service` can publish a `user_mentioned` event without needing to know which service will handle it. The `notification-service` subscribes to this event and sends a notification.

---

## Example Request Flow: User Creates a New Post

1.  A logged-in user sends a request from the client app to create a post. The request includes the JWT in the authorization header.
2.  The request first hits the **API Gateway** (`:8888`).
3.  The **API Gateway** validates the JWT. If valid, it forwards the request to the **Post Service** (`:8083`).
4.  The **Post Service** receives the request, processes the content, and saves the new post to the **MongoDB** database.
5.  If the post mentions another user, the **Post Service** publishes a `USER_MENTIONED` event to a topic in **Kafka**.
6.  The **Notification Service**, which is subscribed to that Kafka topic, receives the event.
7.  The **Notification Service** processes the event, constructs a notification message, and uses the Brevo API to send an email to the mentioned user.

This entire process happens in milliseconds, and the microservices architecture ensures that each service handles its part of the job independently.
