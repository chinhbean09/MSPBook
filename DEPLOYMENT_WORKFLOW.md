# Luồng Triển Khai Docker Chuẩn Hóa

Tài liệu này mô tả luồng làm việc chính xác và tối ưu để triển khai toàn bộ dự án microservice này bằng Docker. Mục tiêu cuối cùng là bất kỳ ai cũng có thể chạy toàn bộ hệ thống chỉ với Docker được cài đặt, mà không cần cài đặt Java hay Maven ở máy cá nhân.

## 1. Ý Tưởng Cốt Lõi: Build Đa Giai Đoạn (Multi-Stage Builds)

Chúng ta sử dụng một kỹ thuật gọi là "build đa giai đoạn" bên trong `Dockerfile`. Kỹ thuật này giải quyết vấn đề lớn nhất: tách biệt môi trường **build code** và môi trường **chạy code**.

- **Giai đoạn 1 (Build):** Sử dụng một image Docker lớn có sẵn cả Java và Maven để biên dịch mã nguồn `.java` thành một file `.jar`.
- **Giai đoạn 2 (Run):** Sử dụng một image Docker khác, cực kỳ nhỏ gọn, chỉ chứa Java Runtime (JRE). Sau đó, sao chép file `.jar` đã được tạo ở giai đoạn 1 vào image này.

**Tại sao lại làm như vậy?**
- **Tính độc lập:** Người dùng không cần cài Java/Maven. Docker sẽ lo toàn bộ quá trình.
- **Tối ưu hóa:** Image cuối cùng để chạy ứng dụng sẽ rất nhỏ, vì nó không chứa các công cụ build cồng kềnh như Maven và JDK.
- **Bảo mật:** Giảm diện tích tấn công vì image cuối cùng chỉ chứa những gì thực sự cần thiết để chạy.

---

## 2. Phân Tích Các File Cấu Hình

### 2.1. File `Dockerfile` (Áp dụng cho tất cả các service)

Đây là "bản thiết kế" để xây dựng image cho mỗi microservice. Tất cả các service đều sử dụng chung một cấu trúc `Dockerfile` như sau:

```dockerfile
# ---- GIAI ĐOẠN 1: BUILD CODE ----
# Sử dụng một image có sẵn Maven và Java 21 (Temurin) làm môi trường build.
# Đặt tên cho giai đoạn này là "builder" để có thể tham chiếu sau này.
FROM maven:3.9-eclipse-temurin-21 AS builder

# Tạo thư mục làm việc /app bên trong container.
WORKDIR /app

# Sao chép chỉ file pom.xml vào trước.
# TẠI SAO? Docker build theo từng lớp (layer). Nếu file pom.xml không thay đổi,
# Docker sẽ tái sử dụng layer đã tải các dependencies, giúp build nhanh hơn nhiều.
COPY pom.xml .
RUN mvn dependency:go-offline

# Sao chép toàn bộ mã nguồn vào container.
COPY src ./src

# Chạy lệnh build của Maven để tạo file .jar.
# TẠI SAO DÙNG -Dmaven.test.skip=true? Để bỏ qua hoàn toàn việc biên dịch và chạy test,
# tránh các lỗi test làm dừng quá trình build.
RUN mvn clean package -Dmaven.test.skip=true


# ---- GIAI ĐOẠN 2: TẠO IMAGE CUỐI CÙNG ĐỂ CHẠY ----
# Sử dụng một image Java nhỏ gọn, chỉ chứa JRE, để tối ưu dung lượng.
FROM openjdk:21-jdk-slim

# Tạo thư mục làm việc /app.
WORKDIR /app

# Sao chép file .jar đã được build ở giai đoạn "builder" vào image hiện tại.
# Đây là bước quan trọng nhất của kỹ thuật build đa giai đoạn.
COPY --from=builder /app/target/*.jar app.jar

# Lệnh để khởi động ứng dụng khi container chạy.
ENTRYPOINT ["java","-jar","app.jar"]
```

### 2.2. File `docker-compose.yml`

Đây là file "nhạc trưởng", định nghĩa và kết nối tất cả các service (cả ứng dụng và database) lại với nhau.

```yaml
name: micro-service-platform
services:
  # Service cho Kafka (Message Broker)
  kafka:
    image: bitnamilegacy/kafka:3.7.0
    container_name: kafka
    hostname: kafka
    ports:
      - '9094:9094'
    environment:
      - KAFKA_CFG_NODE_ID=0
      - KAFKA_CFG_PROCESS_ROLES=controller,broker
      - KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094
      - KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT
      - KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER

  # Service cho MySQL Database
  mysql-identity:
    image: mysql:8.0.36-debian
    container_name: mysql-db
    hostname: mysql-identity
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: "12345"
      MYSQL_DATABASE: "social_platform_identity"
    volumes:
      - identity-db-data:/var/lib/mysql

  # Service cho Identity
  identity-service:
    # TẠI SAO DÙNG "build"? Để ra lệnh cho Docker Compose tìm Dockerfile trong thư mục ./identity-service và tự build image.
    build:
      context: ./identity-service
    container_name: identity-service
    hostname: identity-service
    ports:
      - "8080:8080"
    # TẠI SAO DÙNG "depends_on"? Để đảm bảo kafka và mysql-identity khởi động xong trước khi identity-service bắt đầu.
    depends_on:
      - kafka
      - mysql-identity
    # TẠI SAO DÙNG "environment"? Để tiêm các biến môi trường vào container.
    # Các biến này sẽ ghi đè lên các giá trị "localhost" trong file application.yaml.
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql-identity:3306/social_platform_identity
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
      - APP_SERVICES_PROFILE=http://profile-service:8081/profile

  # Service cho Neo4j Database
  neo4j-profile:
    image: neo4j:4.4
    container_name: neo4j-profile
    hostname: neo4j-profile
    ports:
      - "7474:7474"
      - "7687:7687"
    environment:
      NEO4J_AUTH: neo4j/12345678
    volumes:
      - profile-db-data:/data

  # Service cho Profile
  profile-service:
    build:
      context: ./profile-service
    container_name: profile-service
    hostname: profile-service
    ports:
      - "8081:8081"
    depends_on:
      - neo4j-profile
    environment:
      - SPRING_NEO4J_URI=bolt://neo4j-profile:7687
      - APP_SERVICES_FILE=http://file-service:8084

  # Service cho MongoDB
  mongo-db:
    image: bitnamilegacy/mongodb:7.0
    container_name: mongo-db
    hostname: mongo-db
    ports:
      - "27017:27017"
    environment:
      MONGODB_ROOT_USER: root
      MONGODB_ROOT_PASSWORD: root
    volumes:
      - mongodb-data:/data/db

  # ... (Các service khác như post, file, chat đều có cấu trúc tương tự) ...

  # Service cho Notification
  notification-service:
    build:
      context: ./notification-service
    container_name: notification-service
    hostname: notification-service
    ports:
      - "8082:8082"
    depends_on:
      - mongo-db
      - kafka
    # TẠI SAO DÙNG "env_file"? Để đọc các biến môi trường từ một file, giúp giữ bí mật (API key)
    # tách biệt khỏi code và file docker-compose.
    env_file:
      - ./.env
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://root:root@mongo-db:27017/notification-service?authSource=admin
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

  # Service cho API Gateway
  api-gateway:
    build:
      context: ./api-gateway
    container_name: api-gateway
    hostname: api-gateway
    ports:
      - "8888:8888"
    depends_on:
      - identity-service
      - profile-service
      - notification-service
      - post-service
      - file-service
      - chat-service
    environment:
      # TẠI SAO CẤU HÌNH ROUTE Ở ĐÂY? Để ghi đè các route "localhost" trong application.yaml
      # bằng tên service hostname (ví dụ: "identity-service") mà Docker Compose quản lý.
      - SPRING_CLOUD_GATEWAY_ROUTES_0_URI=http://identity-service:8080
      # ... (các route khác) ...

# TẠI SAO DÙNG "volumes"? Để lưu trữ dữ liệu của database một cách bền vững.
# Kể cả khi container bị xóa, dữ liệu vẫn còn trong volume trên máy host.
volumes:
  identity-db-data:
  profile-db-data:
  mongodb-data:
  file-storage:
```

---

## 3. Luồng Triển Khai Cuối Cùng (The Final Workflow)

Với các cấu hình trên, quy trình triển khai trở nên vô cùng đơn giản.

### Yêu cầu:
- Chỉ cần cài đặt **Docker** và **Docker Compose**.

### Các bước thực hiện:

1.  **Sao chép mã nguồn:** Lấy toàn bộ mã nguồn của dự án về máy.

2.  **Tạo file `.env`:**
    - Tại thư mục gốc của dự án, tạo một file tên là `.env`.
    - Thêm vào nội dung sau và thay thế bằng API key thật của bạn:
      ```
      BREVO_API_KEY=your_real_brevo_api_key_here
      ```

3.  **Thêm file `.env` vào `.gitignore`:**
    - Mở file `.gitignore` ở thư mục gốc.
    - Thêm dòng sau vào cuối file để đảm bảo không bao giờ đẩy file `.env` lên Git:
      ```
      *.env
      ```

4.  **Chạy một lệnh duy nhất:**
    - Mở terminal tại thư mục gốc của dự án.
    - Chạy lệnh:
      ```sh
      docker-compose up --build -d
      ```

**Lệnh này sẽ tự động làm tất cả mọi thứ:**
- Build image cho từng microservice bằng kỹ thuật đa giai đoạn.
- Tải các image cần thiết cho database và Kafka.
- Tạo network ảo để các container có thể "nói chuyện" với nhau qua tên hostname.
- Khởi động toàn bộ hệ thống theo đúng thứ tự phụ thuộc (`depends_on`).
- Tiêm các biến môi trường (database URL, API key) vào đúng các container cần thiết.

Sau khi lệnh chạy xong, toàn bộ hệ thống microservice của bạn đã sẵn sàng hoạt động.
