import Vuex from 'vuex';

export default new Vuex.Store({
  state: {
    isLoggedIn: false,
    userEmail: '',
    userFirstName: '',
    userLastName: '',
    userPhoto: '',

    allTasks: {
    "1": { 
        id: 1, project_id: 3, title: 'Task 1', description: 'Complete project FROM THE DATABASE', dueDate: '2024-09-30', 
        priority: 'High', completed: false, assignees: ['User A', 'User B', 'User C'], task_assigner: 'User C'
    },
    "2" : { 
        id: 2, project_id: 5, title: 'Task 2', description: 'Prepare presentation', dueDate: '2024-10-05', 
      priority: 'High', completed: false, assignees: ['User C', 'User A'], task_assigner: 'User A'
    },
    "3" : { 
        id: 3, project_id: 2, title: 'Task 3', description: 'DO whatever I want to', dueDate: '2024-11-15', 
      priority: 'High', completed: false, assignees: ['User D', 'User B', 'User A'], task_assigner: 'User A' 
    }
  }
  
  },

  mutations: {
    login(state, userEmail) {
      const userAccounts = JSON.parse(localStorage.getItem('userAccounts'));
      const user = userAccounts.find(account => account.email === userEmail);
      if (user) {
        state.isLoggedIn = true;
        state.userEmail = user.email;
        state.userFirstName = user.firstName;
        state.userLastName = user.lastName;
        state.userPhoto = user.profilePicture;
      } else {
        state.isLoggedIn = false;
        state.userEmail = '';
        state.userFirstName = '';
        state.userLastName = '';
        state.userPhoto = '';
      }
    },

    logout(state) {
      state.isLoggedIn = false;
      state.userEmail = '';
      state.userFirstName = '';
      state.userLastName = '';
      state.userPhoto = '';
    }, 

    setNewTaskAssignee(state, taskId, newAssigner) {
      const t = String(taskId);
      if (newAssigner) {
        state.allTasks.t.task_assigner = newAssigner;
      } else {
        state.allTasks.t.task_assigner = "ayiyooo";
      }
      
    }

  },
  actions: {
    login({ commit }, userEmail) {
      commit('login', userEmail);
    },
    logout({ commit }) {
      localStorage.removeItem('CurrentUser')
      commit('logout');
    },
  },

  plugins: [
    store => {
      store.subscribe((_mutations, state) => {
        localStorage.setItem('vuex', JSON.stringify(state));
      });
    }
  ]
});