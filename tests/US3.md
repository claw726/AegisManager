# As a task assignee, I would like to update the completion status of my individual tasks.  - Test Plan

## Test Case 1: Verify only task assigners can see edit and delete user UI.
1. Log in as the task assignee
2. Navigate to the To-Do List
3. Navigate to a task
4. Verify task does not have edit and delete user UI.
5. Log in as the task assigner
6. Navigate to the To-Do List
7. Navigate to a task
8. Verify task does have edit and delete user UI.

## Test Case 2: Verify task assigners can delete users.
1. Log in as the task assigner
2. Navigate to the To-Do List
3. Navigate to a task
4. Verify task has delete UI.
5. Input assignee email
6. Navigate to task again to verify user is deleted.

## Test Case 3: Verify task assigners can add users.
1. Log in as the task assigner
2. Navigate to the To-Do List
3. Navigate to a task
4. Verify task has add UI.
5. Input assignee email
6. Navigate to task again to verify user is added.

## Test Case 4: Verify task detail updates after users are added or removed.
1. Perform test cases 3 and 4, and at the end of each test case, verify that the task detail has changed.

## Test Case 5: Verify added users can access new task.
1. Log in as the task assigner
2. Navigate to the To-Do List
3. Navigate to a task
4. Add user with add UI.
5. Log in as added user.
6. Navigate to the To-Do List
7. Navigate to a task to check it is added to view.

## Test Case 6: Verify removed users cannot access old task.
1. Log in as the task assigner
2. Navigate to the To-Do List
3. Navigate to a task
4. Remove user with remove UI.
5. Log in as removed user.
6. Navigate to the To-Do List
7. Navigate to a task to check it is not in view.