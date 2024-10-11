import store from '@/store/index.js';

/**
 * Auto-magically logs out users when the close the tab or the browser
 */

export function logout() {

    localStorage.removeItem('CurrentUser');

    store.dispatch('logout');
  }
  
  export function addBeforeUnloadListener() {
    window.addEventListener('beforeunload', logout);
  }
  
  export function removeBeforeUnloadListener() {
    window.removeEventListener('beforeunload', logout);
  }