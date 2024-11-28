# User Story 9 Test - Notification and Invitation Management  

## Test Suite ID: INV-001  
### Feature: Invitation to Organizations, Projects, and Tasks  

---

### 1. Invitation Delivery upon User Addition  
**Test ID:** INV-001-01  
**Objective:** Verify that users are not added immediately to an organization, project, or task and instead receive an invitation.  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**  
1. Log in as an organization, project, or task owner.  
2. Navigate to the Add Users page.  
3. Add a user to the organization, project, or task.  
4. Log in as the added user and navigate to the Invitations page.  

**Expected Results:**  
- The user is not immediately added to the group.  
- An invitation appears on the Invitations page for the user, indicating the group type (organization, project, or task).  
- The invitation includes Accept and Decline buttons.  

---

### 2. Accepting an Invitation  
**Test ID:** INV-001-02  
**Objective:** Verify that accepting an invitation adds the user to the respective group and notifies the inviter.  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**  
1. Log in as the invitation recipient and navigate to the Invitations page.  
2. Locate the invitation and click the Accept button.  
3. Log in as the inviter and check notifications.  
4. Confirm the recipient's access to the organization, project, or task.  

**Expected Results:**  
- The user is added to the respective group.  
- The inviter receives a notification: "[User Name] has accepted your invitation to join [Group Name]."  
- The group appears in the user’s list of organizations, projects, or tasks.  

---

### 3. Declining an Invitation  
**Test ID:** INV-001-03  
**Objective:** Verify that declining an invitation prevents access to the group and notifies the inviter.  
**Acceptance Criteria Reference:** AC3  

**Test Steps:**  
1. Log in as the invitation recipient and navigate to the Invitations page.  
2. Locate the invitation and click the Decline button.  
3. Log in as the inviter and check notifications.  
4. Confirm that the recipient does not gain access to the group.  

**Expected Results:**  
- The user is not added to the respective group.  
- The inviter receives a notification: "[User Name] has declined your invitation to join [Group Name]."  
- The group does not appear in the user’s list of organizations, projects, or tasks.  

---

### 4. Invitation Page UI Behavior  
**Test ID:** INV-001-04  
**Objective:** Verify that the Invitations page displays and segregates invitations based on type (organization, project, task).  

**Test Steps:**  
1. Log in as a user who has received multiple invitations for organizations, projects, and tasks.  
2. Navigate to the Invitations page.  
3. Review the layout and organization of invitations.  

**Expected Results:**  
- Invitations are displayed under separate sections for organizations, projects, and tasks.  
- Each invitation includes clear details:  
  - Group type (organization/project/task)  
  - Group name  
  - Inviter name  

---

### 5. Notification Accuracy for Inviter  
**Test ID:** INV-001-05  
**Objective:** Verify that the inviter receives accurate notifications for both acceptance and declination of invitations.  

**Test Steps:**  
1. Log in as an organization, project, or task owner.  
2. Send an invitation to a user.  
3. Log in as the recipient and accept or decline the invitation.  
4. Log back in as the inviter and check notifications.  

**Expected Results:**  
- Notifications accurately reflect the recipient’s action:  
  - Acceptance: "[User Name] has accepted your invitation to join [Group Name]."  
  - Declination: "[User Name] has declined your invitation to join [Group Name]."  

---

### 6. Backend Functionality for Invitations  
**Test ID:** INV-001-06  
**Objective:** Verify that the backend handles invitation creation, acceptance, and declination correctly.  

**Test Steps:**  
1. Use API endpoints to simulate the following actions:  
   - Create an invitation.  
   - Accept an invitation.  
   - Decline an invitation.  
2. Inspect the backend response for each action.  
3. Check database entries after each action.  

**Expected Results:**  
- Invitations are created with the correct type, recipient, and group details.  
- Accepting an invitation adds the recipient to the group and logs the action.  
- Declining an invitation does not add the recipient to the group and logs the action.  
