// composables/useChat.js
import { ref, onMounted } from 'vue';

export function useChat() {
  const loading = ref(true);
  const error = ref(null);
  const expandedCategories = ref({});

  const loadData = async () => {
    loading.value = true;
    error.value = null;
    try {
      // Load data logic here
      await Promise.all([
        loadOrganizations(),
        loadChats(),
        loadProjects(),
        loadTasks()
      ]);
      restoreExpandedState();
    } catch (err) {
      console.error('Error loading chat data:', err);
      error.value = 'Failed to load chat data. Please try again.';
    } finally {
      loading.value = false;
    }
  };

  const restoreExpandedState = () => {
    const savedState = localStorage.getItem('chatExpandedCategories');
    if (savedState) {
      try {
        expandedCategories.value = JSON.parse(savedState);
      } catch (err) {
        console.error('Error parsing saved state:', err);
        expandedCategories.value = {};
      }
    }
  };

  const saveExpandedState = () => {
    localStorage.setItem(
      'chatExpandedCategories',
      JSON.stringify(expandedCategories.value)
    );
  };

  onMounted(() => {
    loadData();
  });

  return {
    loading,
    error,
    expandedCategories,
    loadData,
    restoreExpandedState,
    saveExpandedState
  };
}