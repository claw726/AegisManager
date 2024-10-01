const organization = {
    state: {
      organizations: []
    },
    mutations: {
      addOrganization(state, organization) {
        state.organizations.push(organization);
      }
    },
    actions: {
      async createOrg({ commit }, { name, description }) {
        const organization = { name, description };
        commit('addOrganization', organization);
      }
    }
  };
  
  export default organization;