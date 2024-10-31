#!/bin/bash

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RESET='\033[0m'

# Define required commands
REQUIRED_COMMANDS=("curl" "magick" "base64" "python3.12")

# Function to check for dependencies
check_dependencies() {
  for cmd in "${REQUIRED_COMMANDS[@]}"; do
    if ! command -v "$cmd" &> /dev/null; then
      echo -e "${RED}Error: $cmd is not installed. Please install it to run this script.${RESET}"
      exit 1
    fi
  done
}

# Check for dependencies
check_dependencies

NUM_USERS=12

# User confirmation
read -p "Are you sure you want to create $NUM_USERS users? (y/n): " confirm
if [[ ! "$confirm" =~ ^[Yy]$ ]]; then
  echo -e "${YELLOW}Operation canceled.${RESET}"
  exit 0
fi

# Set API endpoint URL
API_URL="http://localhost:8080/api/auth/register"

# Function to generate a random name
generate_name() {
  first_names=("John" "Jane" "Bob" "Alice" "Mike" "Emily" "Tom" "Sarah" "David" "Kate")
  last_names=("Smith" "Johnson" "Williams" "Jones" "Brown" "Davis" "Miller" "Wilson" "Moore" "Taylor")
  echo "${first_names[$RANDOM % ${#first_names[@]}]} ${last_names[$RANDOM % ${#last_names[@]}]}"
}

# Function to generate a random email
generate_email() {
  domains=("gmail.com" "yahoo.com" "hotmail.com" "aol.com" "comcast.net" "verizon.net" "att.net" "cox.net" "sbcglobal.net" "bellsouth.net")
  echo "${RANDOM}${RANDOM}@${domains[$RANDOM % ${#domains[@]}]}"
}

# Function to generate a random password
generate_password() {
  echo "${RANDOM}${RANDOM}${RANDOM}"
}
cd "$(dirname "\$0")"
# Set profile pictures folder
PROFILE_PICS_FOLDER=$(dirname "\$0")/profilePics
# Check if profile pictures folder exists
if [ ! -d "${PROFILE_PICS_FOLDER}" ]; then
  echo -e "${RED}Error: ${PROFILE_PICS_FOLDER} folder does not exist.${RESET}"
  exit 1
fi

# Get list of jpg files in profile pictures folder
PROFILE_PICS=($(find "${PROFILE_PICS_FOLDER}" -type f -name "*.jp*g"))

# Check if there are any jpg files in profile pictures folder
if [ ${#PROFILE_PICS[@]} -eq 0 ]; then
  echo -e "${RED}Error: No jpg files found in ${PROFILE_PICS_FOLDER} folder.${RESET}"
  exit 1
fi

# Create users
echo -e "${YELLOW}Creating users...${RESET}"
for ((i=0; i<${#PROFILE_PICS[@]}; i++)); do
  name=$(generate_name)
  email=$(generate_email)
  password=$(generate_password)
  profile_picture='data:image/jpeg;base64,'$(magick "${PROFILE_PICS[$i]}" -resize 256x256\> -quality 80 jpg:- | base64)
  profile_picture=$(echo "${profile_picture}" | python3.12 -c "import sys, urllib.parse; print(urllib.parse.quote(sys.stdin.read()))") 
  response=$(curl -X POST \
    ${API_URL} \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "email=${email}&name=${name}&password=${password}&profilePicture=${profile_picture}")
  
  # Check response and print appropriate message
  if [[ "$response" == *"success"* ]]; then
    echo -e "${GREEN}User created successfully: ${name} (${email})${RESET}"
  else
    echo -e "${RED}Failed to create user: ${name} (${email}) - Response: ${response}${RESET}"
  fi
done
