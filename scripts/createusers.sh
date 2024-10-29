#!/bin/bash

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
  echo "Error: ${PROFILE_PICS_FOLDER} folder does not exist."
  exit 1
fi

# Get list of jpg files in profile pictures folder
PROFILE_PICS=($(find "${PROFILE_PICS_FOLDER}" -type f -name "*.jpg"))

# Check if there are any jpg files in profile pictures folder
if [ ${#PROFILE_PICS[@]} -eq 0 ]; then
  echo "Error: No jpg files found in ${PROFILE_PICS_FOLDER} folder."
  exit 1
fi

# Create users
for ((i=0; i<${#PROFILE_PICS[@]}; i++)); do
  name=$(generate_name)
  email=$(generate_email)
  password=$(generate_password)
  profile_picture=$(magick "${PROFILE_PICS[$i]}" -resize 256x256\> -quality 80 jpg:- | base64)
  curl -X POST \
    ${API_URL} \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    -d "email=${email}&name=${name}&password=${password}&profilePicture=${profile_picture}"
  echo
done