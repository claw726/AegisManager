<template>
  <div
    class="group relative flex items-center p-4 bg-white border rounded-lg hover:shadow-md transition-all duration-200 mb-2"
    :class="{
      'border-blue-200 bg-blue-50/50': isHovered && !isCurrent,
      'border-red-200 bg-red-50/50': isHovered && isCurrent,
    }"
    @mouseenter="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <!-- User Avatar -->
    <div class="relative">
      <img
        v-if="user.profilePicture"
        :src="user.profilePicture"
        :alt="user.userName"
        class="w-12 h-12 rounded-full object-cover border-2"
        :class="avatarBorderClass"
      />
      <div
        v-else
        class="w-12 h-12 rounded-full flex items-center justify-center text-lg font-medium border-2"
        :class="avatarFallbackClass"
      >
        {{ userInitials }}
      </div>
    </div>

    <!-- User Info -->
    <div class="ml-4 flex-grow">
      <h3 class="font-medium text-gray-900">
        {{ user.userName }}
      </h3>
      <p class="text-sm text-gray-500 mt-0.5">
        <i class="fas fa-envelope text-xs mr-1.5 opacity-75"></i>
        {{ user.email }}
      </p>
    </div>

    <!-- Action Button -->
    <button
      class="ml-4 px-4 py-2 rounded-md font-medium text-sm transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2"
      :class="buttonClasses"
      @click.stop="handleUserAction"
    >
      <i :class="buttonIcon" class="mr-2"></i>
      {{ buttonText }}
    </button>
  </div>
</template>

<script>
export default {
  props: {
    user: {
      type: Object,
      required: true,
    },
    isCurrent: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {
      isHovered: false,
    };
  },
  computed: {
    userInitials() {
      return this.user.userName
        .split(" ")
        .map((name) => name[0])
        .join("")
        .toUpperCase();
    },
    buttonClasses() {
      if (this.isCurrent) {
        return {
          "bg-red-50 text-red-600 hover:bg-red-100 focus:ring-red-500": true,
          "scale-105": this.isHovered,
        };
      }
      return {
        "bg-blue-50 text-blue-600 hover:bg-blue-100 focus:ring-blue-500": true,
        "scale-105": this.isHovered,
      };
    },
    buttonIcon() {
      return this.isCurrent ? "fas fa-user-minus" : "fas fa-user-plus";
    },
    buttonText() {
      return this.isCurrent ? "Remove" : "Add";
    },
    avatarBorderClass() {
      if (this.isHovered) {
        return this.isCurrent ? "border-red-300" : "border-blue-300";
      }
      return "border-gray-200";
    },
    avatarFallbackClass() {
      if (this.isHovered) {
        return this.isCurrent
          ? "border-red-300 bg-red-50 text-red-600"
          : "border-blue-300 bg-blue-50 text-blue-600";
      }
      return "border-gray-200 bg-gray-100 text-gray-600";
    },
  },
  methods: {
    handleUserAction() {
      const eventName = this.isCurrent ? "removeUser" : "addUser";
      this.$emit(eventName, this.user.email);
    },
  },
};
</script>