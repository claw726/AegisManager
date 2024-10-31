import { mount } from '@vue/test-utils';
import { createStore } from 'vuex';
import EditProject from '@/views/EditProjectView.vue'; // Adjust the path as necessary
import { createRouter, createWebHistory } from 'vue-router';
import NotificationComponent from '@/components/NotificationComponent.vue';
import NavBar from '@/components/NavBar.vue';
import { beforeEach, describe, it, expect, vi } from "vitest";

const routes = [
    {
        path: '/projects/:projIndex/edit',
        name: 'EditProject',
        component: EditProject,
    }
]


const createStoreWithAuth = (isLoggedIn, currentUser, projectOwnerID) => {
  return createStore({
    modules: {
      auth: {
        state: {
          isLoggedIn,
          currentUser,
        },
      },
      projects: {
        actions: {
          fetchProject: vi.fn(() => Promise.resolve({ projectName: 'Test Project', projectDescription: 'Test Description', projectOwnerID })),
          modifyProject: vi.fn(() => Promise.resolve()),
        },
      },
    },
  });
};

describe('EditProject.vue', () => {
  let store;
  let router;

  beforeEach(() => {
    store = createStoreWithAuth(true, { userID: 'user123' }, 'user123');
    router = createRouter({
        history: createWebHistory(),
        routes,
        });
  });

  it('renders the editable form with current project details', async () => {
    const wrapper = mount(EditProject, {
      global: {
        plugins: [store, router],
        setData: {
            isLoggedIn: true,
        }
      },
    });

    await router.push('/projects/1/edit');
    await wrapper.vm.getProjectData(); // Simulate fetching project data
   
    expect(wrapper.exists()).toBe(true);

    expect(store.state.auth.isLoggedIn).toBe(true);

    const header = wrapper.find('h1');
    expect(header.exists()).toBe(true);
    expect(wrapper.find('h1').text()).toContain('Edit Project: Test Project');
    expect(wrapper.find('#projDescription').element.value).toBe('Test Description');
  });

  it('submits valid project details and updates the project', async () => {
    const wrapper = mount(EditProject, {
      global: {
        plugins: [store, router],
      },
    });

    await router.push('/projects/1/edit');
    await wrapper.vm.getProjectData(); // Simulate fetching project data
    

    // Update project details
    await wrapper.setData({
      modifiedProject: {
        projectName: 'Updated Project',
        projectDescription: 'Updated Description',
        projectOwnerID: 'user123',
      },
    });

    await wrapper.vm.submitForm();

    expect(store.dispatch).toHaveBeenCalledWith('projects/modifyProject', {
      project: {
        projectName: 'Updated Project',
        projectDescription: 'Updated Description',
        projectOwnerID: 'user123',
      },
      projectID: undefined, // Adjust if projectID is set
    });
  });

  it('shows an error message when submitting invalid data', async () => {
    const wrapper = mount(EditProject, {
      global: {
        plugins: [store, router],
      },
    });
    await router.push('/projects/1/edit');
    await wrapper.vm.getProjectData(); // Simulate fetching project data

    // Set invalid project details
    await wrapper.setData({
      modifiedProject: {
        projectName: '', // Invalid name
        projectDescription: 'Updated Description',
        projectOwnerID: 'user123',
      },
    });

    await wrapper.vm.submitForm();

    expect(wrapper.vm.notification.show).toBe(true);
    expect(wrapper.vm.notification.message).toContain('Error determining your identity! Please log out and back in to continue.');
  });

  it('prevents access for unauthorized users', async () => {
    store = createStoreWithAuth(true, { userID: 'user456' }, 'user123'); // Different userID
    const wrapper = mount(EditProject, {
      global: {
        plugins: [store, router],
      },
    });

    await router.push('/projects/1/edit');
    await wrapper.vm.getProjectData(); // Simulate fetching project data

    expect(wrapper.html()).not.toContain('Edit Project:'); // Ensure the edit form is not rendered
  });
});