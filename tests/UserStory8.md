# User Story 8 Test - Access to Private and Deleted Messages

## Test Suite ID: MSG-001
### Feature: Organization Owner Access to Message History

---

### 1. Retention of Deleted Messages
**Test ID:** MSG-001-01  
**Objective:** Verify that deleted messages are retained securely in the system.  
**Acceptance Criteria Reference:** AC1

**Test Steps:**
1. Log in as a regular organization member.
2. Send a private message to another member.
3. Delete the sent message.
4. Log in as the organization owner.
5. Retrieve the message history by clicking the Organization Settings button (Gear icon) and then the View All Chats button.

**Expected Results:**
- Deleted messages are retained in the system.
- Messages are marked as deleted but retrievable.
- All message metadata (timestamps, sender details) is preserved.

---

### 2. Message History Access for Organization Owners
**Test ID:** MSG-001-02  
**Objective:** Verify that organization owners can retrieve all private messages, including deleted ones.  
**Acceptance Criteria Reference:** AC2

**Test Steps:**
1. Log in as the organization owner.
2. Navigate to the organization dashboard.
3. Click the Organization Settings button (Gear icon) and then the View All Chats button.
4. Select a conversation.
5. View all messages, including deleted ones.

**Expected Results:**
- Organization owners can access all messages exchanged between members.
- Deleted messages are clearly marked but fully retrievable.
- Timestamps and metadata are included.

---

### 3. Access Restriction for Non-Owners
**Test ID:** MSG-001-03  
**Objective:** Verify that only organization owners have access to the message history.  
**Acceptance Criteria Reference:** AC3

**Test Steps:**
1. Log in as a regular organization member.
2. Navigate to the organization dashboard.
3. Note the visibility of the Organization Settings button (Gear icon).

**Expected Results:**
- Non-owners cannot view private message history.
- The message history section is not visible to non-owners by way of the Organization settings being inaccessible.

---

### 4. Audit Trail Integrity
**Test ID:** MSG-001-04  
**Objective:** Verify that the retrieved message history is unaltered to ensure a complete audit trail.  
**Acceptance Criteria Reference:** AC4

**Test Steps:**
1. Log in as the organization owner.
2. Retrieve private messages for a conversation.
3. Inspect the data for accuracy (timestamps, sender details, deletion status).

**Expected Results:**
- The retrieved messages include all metadata: timestamps, sender, and deletion status.
- No alterations to message content or metadata are observed.
- The system maintains a complete and accurate audit trail.  