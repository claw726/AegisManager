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

# Build the VueJS app
RUN npm run build

# Stage 2: Serve
FROM node:lts-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/dist /app

# Install the serve package
RUN npm install -g serve

# Expose the port on which the app will run
EXPOSE 8080

# Define the command to start the app
CMD ["serve", "-s", "/app", "-l", "8080"]