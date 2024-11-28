<template>
  <div class="form-group">
    <label class="form-label">
      <i class="fas fa-lock text-primary mr-2"></i>
      {{ Title }}
    </label>

    <div class="relative">
      <input
        :type="showPassword ? 'text' : 'password'"
        v-model="password"
        class="form-input pr-12"
        placeholder="Enter your password"
        @input="checkStrength"
      />
      <button
        type="button"
        @click="togglePassword"
        class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 focus:outline-none"
      >
        <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
      </button>
    </div>

    <!-- Password Strength Indicator -->
    <div class="mt-2">
      <div class="flex justify-between mb-1">
        <span class="text-sm" :class="strengthColorClass">
          {{ strengthText }}
        </span>
        <span class="text-xs text-gray-500">
          {{ password.length }}/{{ minLength }} characters
        </span>
      </div>
      <div class="h-1.5 w-full bg-gray-200 rounded-full overflow-hidden">
        <div
          class="h-full transition-all duration-300"
          :class="strengthBarClass"
          :style="{ width: `${strengthScore * 25}%` }"
        ></div>
      </div>

      <!-- Password Requirements -->
      <div class="mt-3 grid grid-cols-2 gap-2">
        <div v-for="(check, index) in requirements" :key="index"
          class="flex items-center text-sm"
          :class="check.met ? 'text-green-600' : 'text-gray-500'"
        >
          <i :class="check.met ? 'fas fa-check-circle' : 'far fa-circle'" class="mr-2"></i>
          {{ check.text }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    Title: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      password: "",
      showPassword: false,
      strengthScore: 0,
      minLength: 12,
      requirements: [
        { text: "At least 12 characters", met: false },
        { text: "Uppercase letter", met: false },
        { text: "Lowercase letter", met: false },
        { text: "Number", met: false },
        { text: "Special character", met: false },
        { text: "No common patterns", met: false },
      ],
    };
  },
  computed: {
    strengthText() {
      if (this.password.length === 0) return "No Password";
      const scores = ["Very Weak", "Weak", "Medium", "Strong", "Very Strong"];
      return scores[this.strengthScore];
    },
    strengthColorClass() {
      const colors = [
        "text-red-500",
        "text-orange-500",
        "text-yellow-500",
        "text-green-500",
        "text-emerald-600",
      ];
      return colors[this.strengthScore];
    },
    strengthBarClass() {
      const colors = [
        "bg-red-500",
        "bg-orange-500",
        "bg-yellow-500",
        "bg-green-500",
        "bg-emerald-600",
      ];
      return colors[this.strengthScore];
    },
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    checkStrength() {
      // Update requirements
      this.requirements[0].met = this.password.length >= this.minLength;
      this.requirements[1].met = /[A-Z]/.test(this.password);
      this.requirements[2].met = /[a-z]/.test(this.password);
      this.requirements[3].met = /[0-9]/.test(this.password);
      this.requirements[4].met = /[^A-Za-z0-9]/.test(this.password);

      // Check for common patterns
      const commonPatterns = [
        /^[A-Za-z]+\d+$/,  // Only letters followed by numbers
        /^[A-Za-z]+[!@#$%^&*]+$/,  // Only letters followed by symbols
        /12345/,  // Sequential numbers
        /qwerty/i,  // Keyboard patterns
        /password/i,  // Common words
        /admin/i,
        /letmein/i,
        /welcome/i,
      ];
      this.requirements[5].met = !commonPatterns.some(pattern => 
        pattern.test(this.password)
      );

      // Calculate strength score
      let score = 0;
      const metRequirements = this.requirements.filter(req => req.met).length;

      // Base score on met requirements
      score = Math.floor(metRequirements / 2);

      // Additional checks for better scoring
      if (this.password.length >= 16) score += 1;
      if (this.password.length >= 20) score += 1;

      // Entropy bonus for mixed character types
      const hasMultipleCharTypes = 
        (/[A-Z]/.test(this.password) ? 1 : 0) +
        (/[a-z]/.test(this.password) ? 1 : 0) +
        (/[0-9]/.test(this.password) ? 1 : 0) +
        (/[^A-Za-z0-9]/.test(this.password) ? 1 : 0);

      if (hasMultipleCharTypes >= 3) score += 1;

      // Penalize short passwords
      if (this.password.length < 8) score = 0;
      if (this.password.length < 12) score = Math.min(score, 2);

      // Ensure score is within bounds
      this.strengthScore = Math.max(0, Math.min(4, score));

      // Emit the password
      this.$emit("update-password", this.password);
    },
  },
};
</script>

<style scoped>
.form-group {
  @apply space-y-1;
}

.form-label {
  @apply block text-sm font-medium text-gray-700;
}

.form-input {
  @apply w-full px-4 py-3 rounded-lg border border-gray-300 
         focus:ring-2 focus:ring-primary focus:border-primary
         transition-all duration-200 bg-white
         placeholder:text-gray-400;
}

/* Smooth transitions */
.transition-strength {
  transition: all 0.3s ease;
}
</style>