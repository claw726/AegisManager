<template>
  <div
    v-if="!wsConnected"
    class="fixed bottom-4 right-4 bg-red-500 text-white px-4 py-2 rounded-full shadow-lg"
  >
    <i class="fas fa-exclamation-triangle mr-2"></i>
    Reconnecting...
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "ConnectionStatus",
  computed: {
    ...mapState({
      wsConnected: (state) => state.chat.wsConnected,
    }),
  },
};
</script>
