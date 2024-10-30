# Step-by-Step Functional Tests
## Test 1: Delete Button Visibility
**Objective**: Verify that the delete button is visible to the project manager.

1. **Precondition**: Log in as a project manager.
2. **Action**: Navigate to the project settings page.
3. **Expected Result:** The delete button is visible and clearly labeled.

## Test 2: Confirmation Prompt
**Objective**: Verify that a confirmation prompt appears when the delete button is clicked.

1. **Precondition**: Log in as a project manager and navigate to the project settings page.
2. **Action**: Click the delete button.
3. **Expected Result**: A confirmation prompt appears, asking the project manager to confirm the deletion.


## Test 3: Successful Deletion Notification
**Objective**: Verify that a success notification is displayed upon successful project deletion.

1. **Precondition**: Log in as a project manager and navigate to the project settings page.
2. **Action**: Click the delete button, confirm the deletion in the prompt.
3. **Expected Result**: A success notification appears, indicating that the project has been deleted.


## Test 4: Error Handling Notification
**Objective**: Verify that an error notification is displayed if project deletion fails.

1. **Precondition**: Log in as a project manager and navigate to the project settings page.
2. **Action**: Simulate a failure in the backend (e.g., by disconnecting from the server).
3. **Expected Result**: An error notification appears, explaining the issue with project deletion.


## Test 5: Authorization Check
**Objective**: Verify that non-project managers do not see the delete button.

1. **Precondition**: Log in as a user who is not a project manager.
2. **Action**: Navigate to the project settings page.
3. **Expected Result:** The delete button is not visible.

## Test 6: Project List Update
**Objective**: Verify that the project is removed from the project list after successful deletion.

1. **Precondition**: Log in as a project manager and navigate to the project settings page.
2. **Action**: Click the delete button, confirm the deletion in the prompt.
3. **Expected Result**: The project is no longer visible in the project list on the organization dashboard.