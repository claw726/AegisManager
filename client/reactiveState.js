import Vue from 'vue';

export const state = Vue.observable({
    isLoggedIn: !!localStorage.getItem('CurrentUser'),
});

export const mutations = {
    setLoginStatus(status) {
        state.isLoggedIn = status;
    },
};