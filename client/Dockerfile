# Stage 1: Build
FROM node:21-alpine AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Set NPM Configuration for optimization
ENV NODE_ENV=production \
    NPM_CONFIG_PRODUCTION=true \
    NPM_CONFIG_LOGLEVEL=error

# Install the app dependencies with specific optimizations
RUN npm ci --verbose --only=production && \
    npm cache clean --force

# Copy necessary config files
COPY . .

# Build the VueJS app
RUN npm run build

# Stage 2: Serve
FROM nginx:stable-alpine AS production

# Set the working directory inside the container
WORKDIR /app

# Install openssl
RUN apk add --no-cache openssl curl && \
    rm -rf /var/cache/apk/*

# Create SSL directory and generate certificates
RUN mkdir -p /etc/nginx/ssl && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/key.pem \
    -out /etc/nginx/ssl/cert.pem \
    -subj "/C=US/ST=Indiana/L=WestLafayette/O=AegisManager/CN=localhost" \
    -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

# Copy the nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the built app from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Set proper permissions
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html && \
    chown -R nginx:nginx /etc/nginx/ssl && \
    chmod -R 600 /etc/nginx/ssl

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8081/health || exit 1

# Expose the port on which the app will run
EXPOSE 8081 8443

# Define the command to start the app
CMD ["nginx", "-g", "daemon off;"]
