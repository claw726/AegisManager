# User Story 10 Test - Deleting Files Uploaded to a Task  

## Test Suite ID: FILE-003  
### Feature: File Deletion by Task Assignees  

---

### 1. File Upload and Deletion by Assignee  
**Test ID:** FILE-003-01  
**Objective:** Verify that a task assignee can delete files they upload to a task.  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**  
1. Log in as a task assignee.  
2. Navigate to a task where the user is assigned.  
3. Upload a file to the task.  
4. Refresh the task view to ensure the file is displayed.  
5. Use the delete option provided in the UI for the uploaded file.  

**Expected Results:**  
- The uploaded file is deleted successfully.  
- A success message is displayed: "File deleted successfully."  
- The file no longer appears in the uploaded files list.  

---

### 2. UI Restriction for Non-Assignees  
**Test ID:** FILE-003-02  
**Objective:** Verify that users who are not task assignees cannot delete files uploaded to the task.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as a user who is not assigned to the task.  
2. Navigate to a task that has uploaded files.  
3. View the list of uploaded files.  

**Expected Results:**  
- The delete file option is not displayed in the UI.  
- Non-assignees cannot delete any files from the task.  

---

### 3. Task Assigner File Deletion  
**Test ID:** FILE-003-03  
**Objective:** Verify that the task assigner can delete any files associated with the task.  
**Acceptance Criteria Reference:** AC3  

**Test Steps:**  
1. Log in as the task assigner.  
2. Navigate to a task where the user is the assigner.  
3. View the list of uploaded files for the task.  
4. Select and delete files uploaded by any user.  

**Expected Results:**  
- The task assigner can delete any uploaded file.  
- A success message is displayed for each deletion: "File deleted successfully."  
- Deleted files no longer appear in the uploaded files list.  

---

### 4. UI Logic Validation for File Deletion Permissions  
**Test ID:** FILE-003-04  
**Objective:** Verify that the file deletion UI is displayed only to authorized users.  
**Acceptance Criteria References:** AC2, AC3  

**Test Steps:**  
1. Log in as a task assigner and navigate to the task.  
2. Confirm that the delete file option is displayed in the UI for all uploaded files.  
3. Log in as a task assignee who uploaded files to the task and navigate to the task.  
4. Confirm that the delete file option is displayed only for files uploaded by the assignee.  
5. Log in as a user who is not assigned to the task and navigate to the task.  
6. Confirm that the delete file option is not displayed in the UI.  

**Expected Results:**  
- The delete file UI is displayed only to authorized users (assigner or uploader).  
- The UI hides the delete file option from unauthorized users.  

---

### 5. API Permissions Validation  
**Test ID:** FILE-003-05  
**Objective:** Verify that the API enforces deletion permissions.  

**Test Steps:**  
1. Attempt to delete a file using the API while authenticated as the file uploader.  
2. Attempt to delete a file using the API while authenticated as the task assigner.  
3. Attempt to delete a file using the API while authenticated as a user who is not assigned to the task.  

**Expected Results:**  
- The API allows the file uploader to delete their own files.  
- The API allows the task assigner to delete any file.  
- The API denies file deletion requests from unauthorized users with a proper error response, e.g., `403 Forbidden`.  

---

### 6. Audit Trail Validation  
**Test ID:** FILE-003-06  
**Objective:** Verify that all file deletion actions are logged for audit purposes.  

**Test Steps:**  
1. Log in as a task assigner or task assignee and delete a file.  
2. Access system logs or audit trail data.  

**Expected Results:**  
- Deletion actions are logged with the following details:  
  - Task ID  
  - File name  
  - User performing the deletion  
  - Timestamp of the action  
- Logs ensure a complete record of file deletions for audit purposes.  