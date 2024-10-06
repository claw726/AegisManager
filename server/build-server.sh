#!/bin/bash

# Check if the volume already exists
if [ ! $(docker volume ls -q -f name=server_db_data) ]; then
    # Create the Docker volume
    docker volume create server_db_data
fi

# Copy the data into the volume
docker run --rm -v server_db_data:/data -v ./db/aegis.db:/aegis.db alpine sh -c "cp /aegis.db /data/"

# Start the Docker Compose environment
docker-compose up --build