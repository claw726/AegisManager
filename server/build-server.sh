#!/bin/bash

# Check if the volume already exists
if [ ! $(docker volume ls -q -f name=db_data) ]; then
    # Create the Docker volume
    docker volume create db_data
    # Copy the data into the volume
    docker run --rm -v db_data:/data -v ./db/aegis.db:/aegis.db alpine sh -c "cp /aegis.db /data/"
fi

# Start the Docker Compose environment
docker-compose up --build