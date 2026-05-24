FROM maven:3.8.3-openjdk-17-slim
WORKDIR /easy-english

# Install required tools for development
RUN apt-get update && apt-get install -y inotify-tools && rm -rf /var/lib/apt/lists/*

COPY docker-app/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

EXPOSE 8001

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
