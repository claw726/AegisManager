<template>
  <div :class="['relative', width]">
    <div class="relative">
      <input
        v-model="localSearchQuery"
        type="text"
        :placeholder="placeholder"
        class="w-full px-4 py-2 pl-10 pr-4 text-gray-700 bg-white border border-gray-300 rounded-lg focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
        @input="handleInput"
      />
      <div
        class="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none"
      >
        <i class="fas fa-search text-gray-400"></i>
      </div>
      <button
        v-if="localSearchQuery"
        class="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-600"
        @click="clearSearch"
      >
        <i class="fas fa-times"></i>
      </button>
    </div>
    <div v-if="showDateHelp" class="mt-1 text-sm text-gray-500">
      Date format: MM/DD/YYYY
    </div>
  </div>
</template>

<script>
export default {
  name: "SearchComponent",
  props: {
    modelValue: {
      type: String,
      default: "",
    },
    width: {
      type: String,
      default: "w-full",
    },
    placeholder: {
      type: String,
      default: "Search by task name or date (MM/DD/YYYY)",
    },
    showDateHelp: {
      type: Boolean,
      default: true,
    },
  },

  data() {
    return {
      localSearchQuery: this.modelValue,
    };
  },

  watch: {
    modelValue(newValue) {
      this.localSearchQuery = newValue;
    },
  },

  methods: {
    handleInput() {
      this.$emit("update:modelValue", this.localSearchQuery);
    },

    clearSearch() {
      this.localSearchQuery = "";
      this.$emit("update:modelValue", "");
    },
  },
};
</script>