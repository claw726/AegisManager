<template>
  <div class="relative inline-block text-left">
    <!-- Trigger Button -->
    <button
      :class="{ 'ring-2 ring-blue-500 ring-offset-2': isOpen }"
      type="button"
      class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-white border border-gray-200 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all duration-200"
      @click="toggleMenu"
    >
      <i :class="symbol" class="text-gray-600"></i>
    </button>

    <!-- Dropdown Menu -->
    <transition
      enter-active-class="transition duration-100 ease-out"
      enter-from-class="transform scale-95 opacity-0"
      enter-to-class="transform scale-100 opacity-100"
      leave-active-class="transition duration-75 ease-in"
      leave-from-class="transform scale-100 opacity-100"
      leave-to-class="transform scale-95 opacity-0"
    >
      <div
        v-if="isOpen"
        v-click-outside="closeMenu"
        class="absolute right-0 mt-2 w-56 rounded-lg shadow-lg bg-white ring-1 ring-black ring-opacity-5 divide-y divide-gray-100 focus:outline-none z-50"
      >
        <div class="py-1">
          <button
            v-for="(item, index) in items"
            :key="index"
            class="group flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-700 transition-colors duration-150"
            @click="handleItemClick(item)"
          >
            <!-- Item Icon (if provided) -->
            <i
              :class="item.symbol"
              class="mr-3 text-gray-400 group-hover:text-blue-500"
            ></i>

            <!-- Item Title -->
            <span class="flex-1">{{ item.title }}</span>

            <!-- Right Arrow for items with sub-menus -->
            <svg
              v-if="item.subItems"
              class="ml-2 h-4 w-4 text-gray-400 group-hover:text-blue-500"
              viewBox="0 0 20 20"
              fill="currentColor"
            >
              <path
                fill-rule="evenodd"
                d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
                clip-rule="evenodd"
              />
            </svg>
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: "DropdownMenu",
  directives: {
    "click-outside": {
      mounted(el, binding) {
        el.clickOutsideEvent = function (event) {
          // Add check to prevent closing when clicking the trigger button
          const triggerButton = el.previousElementSibling;
          if (
            !(el === event.target || el.contains(event.target)) &&
            !(
              triggerButton === event.target ||
              triggerButton.contains(event.target)
            )
          ) {
            binding.value(event);
          }
        };
        document.addEventListener("click", el.clickOutsideEvent);
      },
      unmounted(el) {
        document.removeEventListener("click", el.clickOutsideEvent);
      },
    },
  },
  props: {
    items: {
      type: Array,
      required: true,
    },
    symbol: {
      type: String,
      required: false,
      default: "fas fa-ellipsis-v",
    },
  },
  data() {
    return {
      isOpen: false,
    };
  },
  mounted() {
    // Close menu on escape key
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape" && this.isOpen) {
        this.closeMenu();
      }
    });
  },
  beforeUnmount() {
    document.removeEventListener("keydown", this.closeMenu);
  },
  methods: {
    toggleMenu(event) {
      // Stop event propagation
      event.stopPropagation();
      this.isOpen = !this.isOpen;
    },
    closeMenu() {
      this.isOpen = false;
    },
    handleItemClick(item) {
      if (item.command) {
        item.command();
      }
      this.closeMenu();
    },
  },
};
</script>
