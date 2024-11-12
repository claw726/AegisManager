# Stage 1: Build
FROM node:current-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Set build arguments and environment variables
ARG NODE_ENV=production
ARG NPM_CONFIG_LOGLEVEL=error

ENV NODE_ENV=${NODE_ENV} \
    NPM_CONFIG_PRODUCTION=true \
    NPM_CONFIG_LOGLEVEL=${NPM_CONFIG_LOGLEVEL} \
    PATH=/app/node_modules/.bin:$PATH

# Use BuildKit's cache mount for npm
RUN --mount=type=cache,target=/root/.npm \
    npm set cache /root/.npm

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install dependencies with cache
RUN --mount=type=cache,target=/root/.npm \
    npm ci --verbose --only=production

# Copy necessary config files
COPY . .

# Build the VueJS app with cache
RUN --mount=type=cache,target=/root/.npm \
    npm run build

# Stage 2: Serve
FROM nginx:alpine-slim AS production

# Set the working directory inside the container
WORKDIR /app

# Install required packages and clean up in one layer
RUN --mount=type=cache,target=/var/cache/apk \
    apk update && \
    apk add --no-cache \
    openssl \
    curl \
    tini && \
    mkdir -p /etc/nginx/ssl && \
    mkdir -p /var/cache/nginx && \
    # Create nginx dirs with correct perms
    chown -R nginx:nginx /etc/nginx && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid && \
    # Ensure nginx user can read the SLL dir
    chmod -R 755 /etc/nginx/ssl

# Generate SSL certificates using BuildKit's secret mount
RUN --mount=type=secret,id=ssl_password,mode=0400 \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/key.pem \
    -out /etc/nginx/ssl/cert.pem \
    -subj "/C=US/ST=Indiana/L=WestLafayette/O=AegisManager/CN=localhost" \
    -addext "subjectAltName=DNS:localhost,IP:127.0.0.1,IP:0.0.0.0" &&\
    chown nginx:nginx /etc/nginx/ssl/key.pem && \
    chown nginx:nginx /etc/nginx/ssl/cert.pem && \
    chmod 600 /etc/nginx/ssl/key.pem && \
    chmod 600 /etc/nginx/ssl/cert.pem

# Copy nginx configuration
COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder stage
COPY --from=builder --chown=nginx:nginx /app/dist /usr/share/nginx/html

# Set proper permissions
RUN chmod -R 755 /usr/share/nginx/html

# Use tini as init
ENTRYPOINT ["/sbin/tini", "--"]

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -k https://localhost:8443/health || exit 1

# Expose the port on which the app will run
EXPOSE 8081 8443

# Define the command to start the app
CMD ["nginx", "-g", "daemon off;"]
