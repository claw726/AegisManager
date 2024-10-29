<template>
  <div class="search-container" :style="{ width: size }">
    <i class="fas fa-search search-icon"></i>
    <input
        type="text"
        :value="searchQuery"
        @input="onInput"
        placeholder="Search..."
        class="search-input"
    />
    <button v-if="searchQuery" @click="clearSearch" class="clear-button">×</button>
  </div>
</template>

<script>
export default {
  props: {
    searchQuery: {
      type: String,
      default: "",
    },
    size: {
      type: String,
      default: "100%",
    },
  },
  methods: {
    onInput(event) {
      this.$emit("update:searchQuery", event.target.value);
    },
    clearSearch() {
      this.$emit("update:searchQuery", "");
    },
  },
};
</script>

<style scoped>
.search-container {
  position: relative;
  margin-bottom: 1rem;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #ccc;
}

.search-input {
  width: 100%;
  padding: 0.5rem 0.5rem 0.5rem 2rem; /* Adjust padding to make space for the icon */
  border: 1px solid #ccc;
  border-radius: 4px;
}

.clear-button {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #ccc;
}

.clear-button:hover {
  color: #000;
}
</style>