#!/bin/bash

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RESET='\033[0m'

# API URL
API_URL="http://localhost:8080"

# Function to log in the user
login() {
  read -p "Enter your email: " email
  read -sp "Enter your password: " password
  echo

  # Prepare login parameters
  params="email=${email}&password=${password}"

  # Perform login
  response=$(curl -s -X POST "${API_URL}/api/auth/login" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "${params}")

  # Check for errors in the response
  if [[ $? -ne 0 ]]; then
    echo -e "${RED}Error: Unable to connect to the server.${RESET}"
    exit 1
  fi

   # Print the response for debugging
  echo "Response: $response"

  # Extract session key from the response
  session_key=$(echo "$response" | jq -r '.token')

  if [[ "$session_key" == "null" ]]; then
    error_message=$(echo "$response" | jq -r '.message // "Login failed. No token received."')
    echo -e "${RED}Error: ${error_message}${RESET}"
    exit 1
  fi

  echo -e "${GREEN}Login successful! Session key: ${session_key}${RESET}"
  echo "$session_key"
}

# Function to create an organization
create_organization() {
  local session_key=\$1
  org_name="Photography Studio"
  org_description="A professional photography studio specializing in various photography styles."
  
  # Encode the organization logo in base64
  profile_picture='data:image/jpeg;base64,'$(magick "./photos/org.jpg" -resize 256x256\> -quality 80 jpg:- | base64)
  profile_picture=$(echo "${profile_picture}" | python3.12 -c "import sys, urllib.parse; print(urllib.parse.quote(sys.stdin.read()))") 

  # Prepare organization parameters
  org_params="orgName=${org_name}&orgDescription=${org_description}&orgOwnerID=1&encodedImage=${org_logo}"

  # Perform organization creation
  response=$(curl -s -X POST "${API_URL}/api/orgs/createOrg" \
    -H "Authorization: Bearer ${session_key}" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "${org_params}")

  if [[ $? -ne 0 ]]; then
    echo -e "${RED}Error: Unable to connect to the server while creating organization.${RESET}"
    exit 1
  fi

  # Check for success
  if [[ $(echo "$response" | jq -r '.success') == "true" ]]; then
    echo -e "${GREEN}Organization '${org_name}' created successfully!${RESET}"
  else
    error_message=$(echo "$response" | jq -r '.message // "Failed to create organization."')
    echo -e "${RED}Error: ${error_message}${RESET}"
    exit 1
  fi
}

# Function to create a project
create_project() {
  local session_key=\$1
  local parent_org_id=\$2
  local project_name=\$3
  local project_description=\$4
  local project_image_path=\$5

  # Encode the project image in base64
  project_image='data:image/jpeg;base64,'$(magick "${project_image_path}" -resize 256x256\> -quality 80 jpg:- | base64)
  project_image=$(echo "${project_image}" | python3.12 -c "import sys, urllib.parse; print(urllib.parse.quote(sys.stdin.read()))") 

  # Prepare project parameters
  project_params="projectName=${project_name}&projectDescription=${project_description}&projectOwnerID=1&parentOrgID=${parent_org_id}&encodedImage=${project_image}"

  # Perform project creation
  response=$(curl -s -X POST "${API_URL}/api/projects/createProject" \
    -H "Authorization: Bearer ${session_key}" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "${project_params}")

  if [[ $? -ne 0 ]]; then
    echo -e "${RED}Error: Unable to connect to the server while creating project '${project_name}'.${RESET}"
    exit 1
  fi

  # Check for success
  if [[ $(echo "$response" | jq -r '.success') == "true" ]]; then
    echo -e "${GREEN}Project '${project_name}' created successfully!${RESET}"
  else
    error_message=$(echo "$response" | jq -r '.message // "Failed to create project."')
    echo -e "${RED}Error: ${error_message}${RESET}"
    exit 1
  fi
}

# Main script execution
session_key=$(login)

# Create organization
create_organization "$session_key"

# Get the organization ID for project creation
org_id=2

# Create three projects
create_project "$session_key" "$org_id" "Wedding Photography" "Capturing the beautiful moments of weddings." "./photos/weddingPhoto.jpg"
create_project "$session_key" "$org_id" "Astrophotography" "Exploring the beauty of the night sky." "./photos/astrophotography.jpg"
create_project "$session_key" "$org_id" "Professional Photography" "High-quality professional photography services." "./photos/professional.jpg"

echo -e "${GREEN}All operations completed successfully!${RESET}"