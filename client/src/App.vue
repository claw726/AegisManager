<!-- Container for the application -->
<template>
  <router-view />
</template>

<script>
import WebSocketService from "@/services/websocket.js";

export default {
  name: "App",
  components: {},

  created() {
    WebSocketService.connect();
  },

  beforeDestroy() {
    WebSocketService.disconnect();
  },
};
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: "montserrat", sans-serif;
}
</style>