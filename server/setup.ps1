# Check if the Docker volume already exists
$volumeExists = docker volume ls -q -f name=db_data

if (-not $volumeExists) {
    # Create the Docker volume
    docker volume create db_data

    # Copy the data into the volume
    docker run --rm -v db_data:/data -v ${PWD}\db\aegis.db:/aegis.db alpine sh -c "cp /aegis.db /data/"
}

# Start the Docker Compose environment
docker-compose up --build