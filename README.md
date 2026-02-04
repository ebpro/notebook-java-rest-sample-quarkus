# Learning Guide — Java REST Quarkus Product Catalog (Progressive Concepts)

This repository is an incremental, illustrative Quarkus-based product catalog that demonstrates progressive API and architecture concepts across multiple versions. Use this guide to explore the codebase systematically, understand the evolution of design choices (API versioning, DTOs, mapping, persistence, service refactoring), and practice extension exercises.

## Learning Objectives

- Understand progressive API design and versioning strategies.
- Observe separation of concerns: API layer, DTOs, mappers, domain, persistence, services.
- Learn how to refactor services and repositories across iterations.
- Explore Quarkus development & packaging (dev mode, JVM packaging, native image artifact present).
- Examine project containerization and integration with Postgres, Traefik, and Prometheus in compose files.

## Quick Prerequisites

- Java 21+ (as required by Quarkus in this project). Verify `./mvnw -v`.
- Docker & Docker Compose for full integration stack (Postgres, Traefik, Prometheus).

## Quick Start (run locally)

### Run the database in Docker and the application locally (dev mode or packaged)

Run the application in Quarkus dev mode (recommended while exploring):

Set the minimal environment variables:

```bash
export DB_NAME=products
export DB_USER=tpuser
export DB_PASSWORD=Tp@2026
export DB_HOST=localhost
export DB_PORT=5432
export HOST_POSTGRES_PORT=15432
export HOST_HTTP_PORT=8080
```

start a Postgres instance (if not using Docker Compose):

```bash
docker run --name product-catalog-postgres \
    -e POSTGRES_DB=$DB_NAME \
    -e POSTGRES_USER=$DB_USER \
    -e POSTGRES_PASSWORD=$DB_PASSWORD \
    -p $HOST_POSTGRES_PORT:$DB_PORT \
    -d postgres:16-alpine
```

Then run Quarkus in dev mode:

```bash
./mvnw quarkus:dev
```

or with the Quarkus CLI if installed:

```bash
quarkus dev
```

You can now call the API endpoints (e.g., `http://localhost:8080/api/v1/products`) see [products.http](products.http) for sample requests.
check the `@baseUrl` variable at the top to switch between environments.

Build a JVM package (fast):

```bash
./mvnw package -DskipTests
java -jar target/quarkus-app/quarkus-run.jar
```

stop and clean up the Postgres container when done:

```bash
docker stop product-catalog-postgres
docker rm product-catalog-postgres
```

## Run both the database and the application in Docker

Set the minimal environment variables:

Note: here `DB_HOST` is set to the Docker container name `product-catalog-postgres` since both containers will be in the same Docker network.
and that the `DB_PORT` is the internal container port (5432) while `HOST_POSTGRES_PORT` is the host machine port mapped to it.

```bash
export DB_NAME=products
export DB_USER=tpuser
export DB_PASSWORD=Tp@2026
export DB_HOST=product-catalog-postgres
export DB_PORT=5432
export HOST_POSTGRES_PORT=15432
export HOST_HTTP_PORT=8080
```

create the Docker network:

```bash
docker network create product-catalog-network
```

start a Postgres instance (if not using Docker Compose):

```bash
docker run --name product-catalog-postgres \
    -e POSTGRES_DB=$DB_NAME \
    -e POSTGRES_USER=$DB_USER \
    -e POSTGRES_PASSWORD=$DB_PASSWORD \
    -p $HOST_POSTGRES_PORT:$DB_PORT \
    --network product-catalog-network \
    -d postgres:16-alpine
```

Build an optimzed jvm package with JIB (container image):

```bash
./mvnw clean package -Pjvm
```

See the image `bruno/product-catalog:1.0.0-jib` created — run it with:

```bash
docker run --name product-catalog-jvm \
    -e DB_NAME=$DB_NAME \
    -e DB_USER=$DB_USER \
    -e DB_PASSWORD=$DB_PASSWORD \
    -e DB_HOST=product-catalog-postgres \
    -e DB_PORT=$DB_PORT \
    -p $HOST_HTTP_PORT:8080 \
    --network product-catalog-network \
    -d bruno/product-catalog:1.0.0-jib
```

stop and clean up the Postgres container, the app container, and the network when done:

```bash
docker stop product-catalog-postgres
docker rm product-catalog-postgres
docker stop product-catalog-jvm
docker rm product-catalog-jvm
docker network rm product-catalog-network
```

### Run the full stack with Docker Compose

The same stack can be managed with Docker Compose.

First clean up the variables that may interfere with Compose. `compose.yml` and `compose.prod.yml` define the required environment variables internally, they can be ovverriden via a `.env` file if needed (see the provided `.env.example`).

```bash
unset DB_NAME
unset DB_USER
unset DB_PASSWORD
unset DB_HOST
unset DB_PORT
unset HOST_POSTGRES_PORT
unset HOST_HTTP_PORT
```

Bring up the minimal stack (Postgres and the application) with Docker Compose (default `compose.yml`):

```bash
docker compose up -d
```

The database and the application will be available at the same ports as above (Postgres on `localhost:15432` and the app on `localhost:8080`).

The stack can be stopped (keeping data) with:

```bash
docker compose down
```

and started again with:

```bash
docker compose up -d
```

the stack can be removed completely (including volumes) with:

```bash
docker compose down -v
```

### (OPTIONAL) Run the full stack with Traefik and monitoring - ADVANCED

Finally we will bring up the full stack with Traefik and monitoring in production.
The stack will start the app and the database as before, but also Traefik as a reverse proxy in front of the app, and Prometheus + Grafana for monitoring.
The reverse proxy will route requests to the app based on the hostname (`products.localhost` for HTTP and `products.localhost:5443` for HTTPS) and will also handle TLS termination with a self-signed certificate (defined in `traefik/dynamic/dynamic.yml`). Prometheus is a monitoring system that will scrape metrics from the app and store them, while Grafana is a visualization tool that will connect to Prometheus and display dashboards.

In real deployments, proper DNS records and valid TLS certificates should be used. For this local setup, we need to tweak the `/etc/hosts` file (or equivalent) to map the custom hostnames to `localhost` for testing purposes (you need to be root or use sudo **BE CAREFUL, you can break your system if you edit this file incorrectly**). Add the following lines to your `/etc/hosts` file to simulate DNS for local testing:

```bash
127.0.0.1 products.localhost
127.0.0.1 traefik.products.localhost
127.0.0.1 prometheus.products.localhost
127.0.0.1 grafana.products.localhost
```

We will need certificates, so we can generate self-signed certificates with the provided script (they will be located in the `traefik/dynamic/certs` directory):

```bash
./traefik/generate-self-signed-certs.sh
```

start the full stack with:

```bash
docker compose -f compose.prod.yml --profile monitoring up -d
```

A profile named `monitoring` is used to include the Prometheus and Grafana services. They are optional and can be excluded if not needed (just omit the `--profile monitoring` part).

The application will be accessible through Traefik at the custom hostnames (n) :

```bash
curl --cacert traefik/dynamic/certs/cert.pem \
   -v \
   -X GET \
   https://products.localhost:5443/api/v1/products
```

It will be available at `https://products.localhost:5443` (note the HTTPS and port 5443, Traefik routing to the app with TLS).

Traefik's dashboard will be available at `https://traefik.products.localhost:5443` with basic auth enabled (username: admin, password: admin).
You must create the password file in `./traefik/secrets/traefik-users` with the following content:

```bash
admin:$2y$05$bq5./xN9KVQtwvHpYayPiOC9fTKt2DVCGo9Y9GHtiv8GCEBBMUpjG
```

or generate it with the `htpasswd` command:

```bash
htpasswd -nbm admin admin > ./traefik/secrets/traefik-users
```

and the credentials are hashed with `htpasswd` using the Apache MD5 algorithm (the provided password hash corresponds to `admin`).

Prometheus and Grafana will also be available at `https://prometheus.products.localhost:5443` and `https://grafana.products.localhost:5443` respectively, with basic auth enabled.

L'application expose des métriques Prometheus à l'endpoint `/metrics` (ex: `https://products.localhost:5443/metrics`) que Prometheus va scraper pour collecter les données de monitoring. Grafana se connecte à Prometheus pour visualiser ces données à travers des dashboards personnalisés.

La stack peut être arrêtée avec :

```bash
docker compose -f compose.prod.yml down
```

et supprimée complètement (y compris les volumes) avec :

```bash
docker compose -f compose.prod.yml down -v
```

## Key files to inspect (After a lecture about JPA, REST APIs, Quarkus basics)

- REST API versions: [src/main/java/org/acme/api/ProductResourceV1.java](src/main/java/org/acme/api/ProductResourceV1.java) — through — [src/main/java/org/acme/api/ProductResourceV5.java](src/main/java/org/acme/api/ProductResourceV5.java)
- Domain model: [src/main/java/org/acme/domain/Product.java](src/main/java/org/acme/domain/Product.java)
- DTOs: [src/main/java/org/acme/dto/ProductDTO.java](src/main/java/org/acme/dto/ProductDTO.java), [src/main/java/org/acme/dto/CreateProductRequest.java](src/main/java/org/acme/dto/CreateProductRequest.java)
- Mapper: [src/main/java/org/acme/mapper/ProductMapper.java](src/main/java/org/acme/mapper/ProductMapper.java)
- Persistence: [src/main/java/org/acme/persistence/ProductEntity.java](src/main/java/org/acme/persistence/ProductEntity.java), [src/main/java/org/acme/persistence/ProductRepository.java](src/main/java/org/acme/persistence/ProductRepository.java), [src/main/java/org/acme/persistence/ProductRepositoryV5.java](src/main/java/org/acme/persistence/ProductRepositoryV5.java)
- Services: [src/main/java/org/acme/service/ProductService.java](src/main/java/org/acme/service/ProductService.java), [src/main/java/org/acme/service/ProductServiceV4.java](src/main/java/org/acme/service/ProductServiceV4.java), [src/main/java/org/acme/service/ProductServiceV5.java](src/main/java/org/acme/service/ProductServiceV5.java)
- Build & infra: `pom.xml`, `compose.yml`, `compose.prod.yml`, `compose.traefik.yml`

### Step-by-step exploration (recommended order)

1) Run the app in dev mode and call endpoints

   - Start dev mode (`./mvnw quarkus:dev`) and explore endpoints exposed by the API versions (V1..V5). Note the base paths and payloads for each version.

2) Compare `ProductResourceV1` → `ProductResourceV5`

   - Open [src/main/java/org/acme/api/ProductResourceV1.java](src/main/java/org/acme/api/ProductResourceV1.java) and then each subsequent version up to [src/main/java/org/acme/api/ProductResourceV5.java](src/main/java/org/acme/api/ProductResourceV5.java).
   - For each version, ask: what changed in request/response models? Did the endpoints switch from exposing domain entities to DTOs? Is there pagination, filtering, or improved error handling added progressively?

3) Inspect DTOs and mappers

   - Study [src/main/java/org/acme/dto/ProductDTO.java](src/main/java/org/acme/dto/ProductDTO.java) and [src/main/java/org/acme/mapper/ProductMapper.java](src/main/java/org/acme/mapper/ProductMapper.java).
   - Exercise: Add a new field to the DTO (e.g., `category`) and propagate it through the mapper and resource version that uses DTOs.

4) Examine persistence and repository evolution

   - Compare [src/main/java/org/acme/persistence/ProductRepository.java](src/main/java/org/acme/persistence/ProductRepository.java) with [src/main/java/org/acme/persistence/ProductRepositoryV5.java](src/main/java/org/acme/persistence/ProductRepositoryV5.java).
   - Questions: Was the persistence model refactored for better queries, for transactions, or for a different storage approach? Note how the entity (`ProductEntity.java`) maps fields and if JPA annotations evolve.

5) Study service layer refactors

   - Open [src/main/java/org/acme/service/ProductService.java](src/main/java/org/acme/service/ProductService.java) and follow changes toward `ProductServiceV4` and `ProductServiceV5`.
   - Identify responsibilities moved into services: validation, orchestration, transaction boundaries, error translation.

6) Cross-cutting concerns

   - Logging and error handling: search for exception mappers or consistent HTTP status mapping.
   - Configuration: review `src/main/resources/application.properties` and `target/classes/application.properties` for datasource and Quarkus settings.

7) Packaging & deployment

   - Look at the `target/` contents and `quarkus-app/` to understand generated artifacts.
   - Use `docker compose` files to learn how the app is expected to be deployed with Postgres and Traefik. Inspect `postgres/init/` for DB initialization SQL.

## Deeper analysis (patterns & rationale)

- API versioning: The sequence V1→V5 showcases an approach to evolve APIs while keeping older versions available — useful for backward compatibility. Study how routing or path versioning is implemented.
- DTOs & mapping: Moving from exposing internal domain objects to DTOs is best practice; it decouples API from internal model and allows stable API contracts.
- Repository evolution: Repositories may get specialized (e.g., V5) to support advanced queries or performance improvements. Look for changed query APIs or additional indexes in migration scripts.
- Service refactoring: Services encapsulate business logic; later versions often move logic out of resources into services for testability and reuse.
- Quarkus usage: The project demonstrates Quarkus dev-mode convenience and packaging for native images — good for microservice performance and fast startup.
