# User Story 11 Test - Deleting Projects with Assigned Tasks  

## Test Suite ID: PRJ-001  
### Feature: Deletion of Projects with Associated Tasks  

---

### 1. Deletion of Projects with Tasks  
**Test ID:** PRJ-001-01  
**Objective:** Verify that projects with tasks can be successfully deleted.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as the project creator.  
2. Create a project and assign tasks to it.  
3. Navigate to the Projects page and locate the created project.  
4. Click the Delete button for the project.  
5. Confirm the deletion when prompted.  

**Expected Results:**  
- The project is removed from the project creator's view.  
- All tasks associated with the project are removed.  
- A success message is displayed to the user.  
- The project and its tasks no longer appear in any user's UI.  

---

### 2. Success Message Display  
**Test ID:** PRJ-001-02  
**Objective:** Verify that a success message is displayed upon project deletion.  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**  
1. Log in as the project creator.  
2. Create a project.  
3. Navigate to the Projects page and locate the created project.  
4. Click the Delete button for the project.  
5. Confirm the deletion when prompted.  

**Expected Results:**  
- A success message is displayed immediately after deletion: "Project and associated tasks successfully deleted."  

---

### 3. UI Update Post-Deletion  
**Test ID:** PRJ-001-03  
**Objective:** Verify that the UI is updated correctly after project deletion.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as the project creator.  
2. Create a project with tasks.  
3. Navigate to the Projects page and delete the project.  
4. Refresh the page or return to the Projects page.  

**Expected Results:**  
- The deleted project and its tasks no longer appear in the project creator's UI.  
- Other users who previously had access to the project or its tasks also no longer see them in their UI.  

---

### 4. Restriction of Deletion to Project Creators  
**Test ID:** PRJ-001-04  
**Objective:** Verify that only the project creator can delete the project.  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**  
1. Log in as a user who is not the creator of the project.  
2. Navigate to the Projects page and locate a project created by another user.  
3. Attempt to delete the project.  

**Expected Results:**  
- Non-creators cannot delete the project.  
- The Delete button is either disabled or not visible to non-creators.

