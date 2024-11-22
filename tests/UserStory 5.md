# User Story 5 Test - Secure File Upload to Tasks  

## Test Suite ID: FILE-001  
### Feature: Upload and Secure Storage of Files to Tasks  

---

### 1. File Upload UI Visibility  
**Test ID:** FILE-001-01  
**Objective:** Verify that the file upload UI is visible on the task view page.  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**  
1. Log in as a user.  
2. Navigate to the task view page for any task.  
3. Inspect the page for the file upload UI.  
4. Click on the files button to access the task files page

**Expected Results:**  
- The file upload UI is visible in the bottom left corner.
- The UI includes a file button that lets you access the files page
- The files page is accessible through the button.

---

### 2. File Selection Dialog Functionality  
**Test ID:** FILE-001-02  
**Objective:** Verify that clicking the upload button on the file selection page opens the file selection dialog.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as a user.  
2. Navigate to the task files page.  
3. Click the "Upload File" button in the file upload UI.  

**Expected Results:**  
- The system opens the file selection dialog.  
- The user can browse and select files from their local directory.  
- The file selection dialog supports all file types.  

---

### 3. File Upload Functionality  
**Test ID:** FILE-001-03  
**Objective:** Verify that users can upload files to a task.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as a user.  
2. Navigate to the task files page page.  
3. Click the "Upload File" button and select a file below 50 MB from the local directory.  
4. Confirm the upload action.  

**Expected Results:**  
- The selected file is uploaded successfully.  
- A success message is displayed: "File uploaded successfully."  
- The file appears in the task's list of uploaded files on the UI.  

---

### 4. File Size Validation  
**Test ID:** FILE-001-04  
**Objective:** Verify that the system prevents uploading files larger than 50 MB.  

**Test Steps:**  
1. Log in as a user.  
2. Navigate to the task files page.  
3. Click the "Upload File" button and select a file larger than 50 MB.  
4. Attempt to upload the file.  

**Expected Results:**  
- The system prevents the upload and displays an error message: "File size exceeds the 50 MB limit."  
- The oversized file does not appear in the task's uploaded files list.  

---

### 5. Backend File Storage Validation  
**Test ID:** FILE-001-05  
**Objective:** Verify that uploaded files are stored securely on the backend.  

**Test Steps:**  
1. Upload a file to a task via the task files page.  
2. Access the backend storage associated with the task.  
3. Check the stored file's metadata (e.g., file name, size, upload timestamp).  

**Expected Results:**  
- The uploaded file is stored in the appropriate location on the backend.  
- File metadata matches the uploaded file details.  
- Files are encrypted or stored securely as per the security requirements.  

---

### 6. File Retrieval via API  
**Test ID:** FILE-001-06  
**Objective:** Verify that files uploaded to a task can be retrieved using the API.  

**Test Steps:**  
1. Use the task ID to call the file retrieval API endpoint.  
2. Inspect the API response for the list of uploaded files.  
3. Attempt to download one of the files using the API-provided URL.  

**Expected Results:**  
- The API returns a list of files uploaded to the task, including their metadata (file name, size, upload date).  
- The API allows secure downloading of the selected file.  

---

### 7. File Upload-Download Integration Test  
**Test ID:** FILE-001-07  
**Objective:** Verify the complete file upload and retrieval flow.  

**Test Steps:**  
1. Log in as a user and upload a file to a task.  
2. Retrieve the file via the API or task view page.  
3. Download the file and verify its contents.  

**Expected Results:**  
- The uploaded file is retrievable via the task view page and the API.  
- The downloaded file matches the originally uploaded file in content and format.  

---

### 8. UI Accessibility and Error Handling  
**Test ID:** FILE-001-08  
**Objective:** Verify that the file upload UI handles accessibility and errors gracefully.  

**Test Steps:**  
1. Attempt to upload a file with a missing or invalid file name.  
2. Attempt to upload a file while disconnected from the internet.  
3. Test the file upload UI using assistive technologies (e.g., screen readers).  

**Expected Results:**  
- The system displays clear error messages for invalid file uploads or connectivity issues.  
- The UI is navigable and usable with assistive technologies.  
