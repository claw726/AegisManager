<template>
  <div class="notification" :class="type">
    <div class="notification-content">
      <slot></slot>
    </div>
    <button class="close-btn" @click="closeNotification">
      &times;
    </button>
  </div>
</template>

<script>
export default {
  props: {
    type: {
      type: String,
      default: 'info' // 'success', 'error', 'warning', etc.
    }
  },
  methods: {
    closeNotification() {
      this.$emit('close');
    }
  }
}
</script>

<style scoped>
.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.notification-content {
  display: flex;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  margin-left: 10px;
}

/* Add styles for different types */
.info {
  background-color: #e0f3fe;
  color: #2196f3;
}

.success {
  background-color: #c8e6c9;
  color: #4caf50;
}

.error {
  background-color: #ffcdd2;
  color: #f44336;
}

/* Add more styles as needed */
</style>