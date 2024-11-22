

# User Story 2 - Direct Messaging Functionality

## Test Suite ID: DM-001
### Feature: Direct Message Initiation and Management

### 1. Search and User Discovery
**Test ID:** DM-001-01  
**Objective:** Verify user search functionality for initiating new direct messages  
**Acceptance Criteria Reference:** AC1  

**Test Steps:**
1. Navigate to the chat interface
2. Click on "New Message" or equivalent button
3. Enter partial username/name in the search field
4. Observe the search results
5. Enter different search terms (partial name, full name, username)
6. Clear search field and repeat

**Expected Results:**
- Search results appear as user types (within 500ms)
- Only users from the same organization are displayed
- Search results show user names and usernames
- Results are filtered to show only users available for direct messaging
- No results message displays when no matches found
- Search works with partial matches
- Search is case-insensitive

### 2. Recent Conversations Display
**Test ID:** DM-001-02  
**Objective:** Verify display and ordering of recent direct message conversations  
**Acceptance Criteria Reference:** AC2  

**Test Steps:**
1. Navigate to the chat page
2. Observe the list of recent direct messages
3. Create a new message in an existing conversation
4. Verify order updates
5. Leave page and return

**Expected Results:**
- Recent conversations are displayed by default
- List is ordered by most recent activity
- Newest messages move conversations to top of list
- List persists between page refreshes
- Timestamps are visible and accurate
- Unread messages are clearly indicated

### 3. Message Delivery and Real-time Updates
**Test ID:** DM-001-03  
**Objective:** Verify real-time message delivery and updates  
**Acceptance Criteria Reference:** AC3  

**Test Steps:**
1. Open a direct message conversation
2. Type a message in the composition field
3. Click the send button
4. Observe message delivery
5. Open the same conversation in another browser/device
6. Verify message appears

**Expected Results:**
- Message appears immediately after sending
- No page refresh required
- Recipient sees message immediately
- Message includes timestamp
