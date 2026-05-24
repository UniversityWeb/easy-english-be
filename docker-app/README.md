# Easy English — Docker quick start

This README explains how to run the Easy English backend locally using the Docker setup in the `docker-app` folder.

## Prerequisites

- Docker and Docker Compose installed
- (Optional) Java and Maven if you want to run locally without Docker

## Files to know

- `docker-compose.db.yml` — services for DB, pgAdmin, Redis, RedisInsight, MinIO
- `docker-compose.yml` — application service for the backend
- `.env` / `.env.example` — environment variables for services
- `docker-entrypoint.sh` — container entrypoint that runs a file-watcher + `mvn spring-boot:run`
- `Dockerfile` — build image used for the backend
- `servers.json` — pgAdmin server connection template

## Env variables

Copy `.env.example` to `.env` (or edit `.env`) and configure values:

Important variables (defaults in example):
- `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`
- `PGADMIN_DEFAULT_EMAIL`, `PGADMIN_DEFAULT_PASSWORD`
- `PROD_DB_URL`, `PROD_DB_USER`, `PROD_DB_PASSWORD`
- Redis: `PROD_REDIS_HOST`, `PROD_REDIS_PORT`, `PROD_REDIS_PASS`
- MinIO: `PROD_MINIO_URL`, `PROD_MINIO_ACCESS_KEY`, `PROD_MINIO_SECRET_KEY`, `PROD_MINIO_BUCKET_NAME`
- JWT and third-party keys: `PROD_JWT_SECRET_KEY`, `PROD_GOOGLE_CLIENT_ID`, `PROD_GOOGLE_CLIENT_SECRET`, `PROD_GEMINI_API_KEY`, etc.

Do NOT commit secrets to version control in production.

### Important: when `.env` changes

If you update values in `.env`, Docker does **not** inject them into an already running container automatically.
You need to recreate the container(s):

```bash
# Recreate only backend app container (no dependency restart)
docker compose --env-file docker-app/.env -f docker-app/docker-compose.yml up -d --force-recreate --no-deps easy-english-app

# Recreate infra containers if you changed DB/Redis/MinIO related vars
docker compose --env-file docker-app/.env -f docker-app/docker-compose.db.yml up -d --force-recreate
```

Quick check that new env is loaded into app container:

```bash
docker exec -it easy-english-app sh -c "printenv | grep -E 'PROD_DB_URL|PROD_REDIS_HOST|SPRING_PROFILES_ACTIVE'"
```

## Start services

Start database + infra services first:

```bash
# from project root
docker-compose -f docker-app/docker-compose.db.yml up -d
```

Start the backend application service (builds image and runs entrypoint):

```bash
docker-compose -f docker-app/docker-compose.yml up --build -d
```

Alternatively bring both up together:

```bash
docker-compose -f docker-app/docker-compose.db.yml -f docker-app/docker-compose.yml up --build -d
```

## Access URLs

- Backend API: http://localhost:8001
- pgAdmin: http://localhost:82
- MinIO Console: http://localhost:9001
- RedisInsight: http://localhost:5540

## Logs and common commands

Tail the backend logs:

```bash
docker-compose -f docker-app/docker-compose.yml logs -f easy-english-app
```

Stop and remove containers:

```bash
docker-compose -f docker-app/docker-compose.db.yml -f docker-app/docker-compose.yml down
```

Remove volumes (will delete DB data):

```bash
docker volume rm easy-english-data minio_data redis_data
```

## File watcher and reload tuning

The `docker-entrypoint.sh` inside the app container runs a background file watcher which does incremental `mvn compile` on changes and then runs the app with `mvn spring-boot:run`.

You can tune watcher behavior with environment variables (defined in the entrypoint):

- `POLL_INTERVAL_SECONDS` (default 5) — how often the watcher polls for file changes
- `DEBOUNCE_INTERVAL_SECONDS` (default 2) — debounce time after first change is detected
- `MIN_COMPILATION_INTERVAL_SECONDS` (default 10) — minimum seconds between successive compilations

Examples (set in `.env` or `docker-compose.yml` service `environment`):

```yaml
services:
  easy-english-app:
    environment:
      POLL_INTERVAL_SECONDS: 5
      DEBOUNCE_INTERVAL_SECONDS: 2
      MIN_COMPILATION_INTERVAL_SECONDS: 10
```

Increase values to reduce frequency of reloads (useful on low-resource machines).

## Development tips

- Mounting the project directory into the container allows live compilation. Keep in mind file system performance on Windows/macOS when using bind mounts.
- If you prefer to run the backend locally (outside Docker), you can set the same environment variables in your shell and run `mvn spring-boot:run`.
- Check `/tmp/compile.log` inside the container for the last compile output when compilation fails.

## Troubleshooting

- pgAdmin cannot connect: check `servers.json` and container name `easy-english-db`, ensure DB container is healthy.
- MinIO login: use the access/secret key from `.env`.
- If containers fail to start due to ports in use, change ports in `docker-compose*.yml`.

## Security note

This configuration is intended for local development. Do not use these defaults (secrets, passwords, simple keys) in production. For production, use a proper secrets manager and secure network configuration.

---

If you want, I can also:
- Add a short `Makefile` with commands to `up`, `down`, `logs`.
- Add a `.env.local` sample for local overrides.

