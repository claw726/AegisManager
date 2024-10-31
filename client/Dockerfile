# Stage 1: Build
FROM node:lts-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install the app dependencies
RUN npm ci

# Copy the rest of the app source code to the working directory
COPY . .

# Enhanced debugging steps
RUN echo "==== Full project structure ====" && \
    find . -type f | sort && \
    echo "==== Content of src/components ====" && \
    ls -la src/components || echo "Directory not found" && \
    echo "==== Content of Enable2FAView.vue ====" && \
    cat src/views/Enable2FAView.vue || echo "File not found"

# Build the VueJS app
RUN npm run build

# Stage 2: Serve
FROM nginx:stable-alpine AS production

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Install openssl
RUN apk add --no-cache openssl

# Generate SSL/TLS certificates
RUN openssl req -x509 -newkey rsa:2048 -nodes -keyout localhost.key -out localhost.crt -days 365 -subj "/C=US/ST=Indiana/L=PurdueUniversity/O=CS307-Group18/CN=localhost"

# Create SSL directory
RUN mkdir -p /etc/nginx/ssl

# Generate self-signed certificate
RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/key.pem \
    -out /etc/nginx/ssl/cert.pem \
    -subj "/C=US/ST=Indiana/L=WestLafayette/O=AegisManager/CN=localhost" \
    -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

# Expose the port on which the app will run
EXPOSE 8081 8443

# Define the command to start the app
CMD ["nginx", "-g", "daemon off;"]
