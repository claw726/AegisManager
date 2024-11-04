# As a task assigner, I should be able to make someone else the task assigner if necessary. - Test Plan

## Test Case 1: Verify task assigners can assign task assigner status.
1. Log in as the task assigner.
2. Navigate to the To-Do List
3. Navigate to a task
4. Verify task has dropdown to reassign the task assigner.
5. Send invitation
6. Log in as assignee into invitation page.
7. Accept invite
8. Navigate to Task and see that task assigner UI is now available. 

## Test Case 2: Verify task assigners can assign task assigner status through invitation only.
1. Log in as the task assigner.
2. Navigate to the To-Do List
3. Navigate to a task
4. Verify task has dropdown to reassign the task assigner.
5. Send invitation
6. Log in as assignee into invitation page.
7. Deny invite
8. Navigate to Task and see that task assigner UI is still not available. 

