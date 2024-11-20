// utils/chatUtils.js
export const computeOrgStructure = (organizations, projects, findChatFn) => {
  if (!organizations || !Array.isArray(organizations)) return [];

  return organizations.map(org => ({
    ...org,
    chat: findChatFn('organization', org.chatID),
    projects: computeProjectStructure(projects, org.orgID, findChatFn)
  }));
};

export const computeProjectStructure = (projects, orgID, findChatFn) => {
  if (!projects || !Array.isArray(projects)) return [];

  return projects
    .filter(project => project && project.parentOrgID === orgID)
    .map(project => ({
      ...project,
      chat: findChatFn('project', project.chatID),
      tasks: computeTaskStructure(project.tasks, findChatFn)
    }));
};

export const computeTaskStructure = (tasks, findChatFn) => {
  if (!tasks || !Array.isArray(tasks)) return [];

  return tasks
    .filter(task => task && task.taskID && task.chatID)
    .map(task => ({
      ...task,
      chat: findChatFn('task', task.chatID)
    }));
};

export const handleChatNavigation = async (chat, router, selectChat, currentUser) => {
  if (!chat?.id) return;

  try {
    await selectChat(chat.id);
    const route = generateChatRoute(chat, currentUser);
    if (route) {
      await router.push(route);
    }
  } catch (err) {
    console.error('Error navigating to chat:', err);
  }
};

export const generateChatRoute = (chat, currentUser) => {
  if (!chat) return null;

  switch (chat.type) {
    case 'direct':
      return {
        name: 'DirectChat',
        params: {
          userID: chat.participants.find(id => id !== currentUser.userID)
        }
      };
    case 'group':
      return {
        name: 'GroupChat',
        params: { groupID: chat.id }
      };
    case 'organization':
      return {
        name: 'OrgChat',
        params: { orgIndex: chat.id.replace('organization-', '') }
      };
    case 'project':
      return {
        name: 'ProjectChat',
        params: { projectIndex: chat.id.replace('project-', '') }
      };
    case 'task':
      return {
        name: 'TaskChat',
        params: { taskIndex: chat.id.replace('task-', '') }
      };
    default:
      return null;
  }
};