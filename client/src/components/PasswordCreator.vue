<!-- Component for handling user's creation of Passwords -->
<template>
  <div class="mb-4">
    <label class="block text-sm font-semibold text-gray-800 mb-2">{{
      Title
    }}</label>
    <input
      type="password"
      v-model="password"
      @input="checkStrength"
      class="w-full border border-highlight rounded-lg p-3"
    />
    <div class="mt-2 text-sm" :class="strengthColor">{{ strengthMessage }}</div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      password: "",
      strengthMessage: "",
      strengthColor: "",
    };
  },
  props: {
    Title: {
      type: String,
      required: true,
    },
  },
  methods: {
    // Update in the future to include more password strength checks
    checkStrength() {
      const length = this.password.length;
      if (length === 0) {
        this.strengthMessage = "";
        this.strengthColor = "";
      } else if (length < 6) {
        this.strengthMessage = "Weak";
        this.strengthColor = "text-red-500";
      } else if (length < 10) {
        this.strengthMessage = "Moderate";
        this.strengthColor = "text-yellow-500";
      } else {
        this.strengthMessage = "Strong";
        this.strengthColor = "text-green-500";
      }
      this.$emit("update-password", this.password);
    },
  },
};
</script>

<style scoped>
/* Add any additional styles here if needed */
</style>
