#!/bin/bash

# Base URL for the API
BASE_URL="http://localhost:8080/api/tasks"

# Determine the OS and set the due date accordingly
if [[ "$OSTYPE" == "darwin"* ]]; then
  # macOS
  dueDate=$(date -v+1w +%Y-%m-%d)
else
  # Linux
  dueDate=$(date -d '+1 week' +%Y-%m-%d)
fi

# Loop through the ranges for parentProjectID and parentOrgID
for parentProjectID in {1..3}; do
  for parentOrgID in {1..3}; do
    for assignerID in {1..5}; do
      # Generate a unique task name and description
      taskName="Task for Project $parentProjectID, Org $parentOrgID, Assigner $assignerID"
      taskDescription="This is a description for task assigned to user $assignerID."

      # Create the task using curl
      response=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/$parentProjectID/update" \
        -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjYWxhdzcyNkBwcm90b24ubWUiLCJpYXQiOjE3MzAxMzQ3NjAsImV4cCI6MTczMDEzNTY2MH0.wyAHIa4ogaNP_eymswFV5n_KUhSKiI0B9l7h3vGJPmm1KUz5V8C_ccbXdKY_ZyxiA344WWgXVrZY_p9w1AdUVQ" \
        -H "Content-Type: application/x-www-form-urlencoded" \
        --data-urlencode "taskName=$taskName" \
        --data-urlencode "taskDescription=$taskDescription" \
        --data-urlencode "assignerID=$assignerID" \
        --data-urlencode "taskPriority=medium" \
        --data-urlencode "dueDate=$dueDate" \
        --data-urlencode "isComplete=false")

      # Check the response status
      if [ "$response" -eq 204 ]; then
        echo "Successfully created task: $taskName"
      else
        echo "Failed to create task: $taskName (HTTP Status: $response)"
      fi
    done
  done
done