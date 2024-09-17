# Use the LTS node Alpine image as the base image
FROM node:lts-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the package.json and package-lock.json files to the working directory
COPY package*.json ./

# Install the app dependencies
RUN npm install

# Copy the rest of the app source code to the working directory
COPY . .

# Build the VueJS app
RUN npm run build

# Expose the port on which the app will run
EXPOSE 8080

# Define the command to start the app
CMD [ "npm", "run", "serve" ]