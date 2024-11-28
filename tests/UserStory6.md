# User Story 6 Test - Task Document Management

## Test Suite ID: FILE-002
### Feature: Viewing and Downloading Documents Attached to Tasks

### 1. Task Document List Display
**Test ID:** FILE-002-01  
**Objective:** Verify that the list of attached documents displays correctly on the task details page.  
**Acceptance Criteria Reference:** AC1

**Test Steps:**
1. Log into the application.
2. Navigate to the task details page for a specific task.
3. Click the Files button.
4. Observe the document list section.
5. Verify that each document displays the file name, type, and size.

**Expected Results:**
- A list of attached documents is visible.
- Each document displays its file name, type, and size.

---

### 2. Document Download Functionality
**Test ID:** FILE-002-02  
**Objective:** Verify that clicking the download button next to a document triggers a file download.  
**Acceptance Criteria Reference:** AC2

**Test Steps:**
1. Log into the application.
2. Navigate to the task details page for a specific task.
3. Click the Files button to view the list of attached documents.
4. Click the download button next to a document.
5. Observe the file download process.

**Expected Results:**
- The file begins downloading immediately upon clicking the button.
- The downloaded file matches the document selected.

---

### 3. Error Handling for Unavailable Documents
**Test ID:** FILE-002-03  
**Objective:** Verify that appropriate error messages are displayed when a document is unavailable.  
**Acceptance Criteria Reference:** AC3

**Test Steps:**
1. Log into the application.
2. Navigate to the task details page for a specific task.
3. Attempt to view or download a document that is unavailable.
4. Observe the application’s response.

**Expected Results:**
- A clear error message is displayed.
- No unexpected behavior, such as crashes or unresponsive UI, occurs. 