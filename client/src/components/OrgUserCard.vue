<template>
  <div class="flex flex-row items-center p-4 border rounded-lg m-4">
    <img
      :src="user.profilePicture"
      alt="🤵"
      class="w-16 h-16 rounded-full drop-shadow-xl mr-4 text-center text-4xl"
    />
    <div class="flex flex-grow">
      <div class="flex flex-col">
        <div class="text-lg font-bold text-gray-800">
          {{ user.userName }}
        </div>
        <div class="text-sm text-gray-500">{{ user.email }}</div>
      </div>
    </div>
    <div class="flex ml-auto">
      <button
        class="dashboard-button"
        v-if="!isCurrent"
        @click.stop="addUser(user)"
      >
        Add
      </button>
      <button class="dashboard-button" v-else @click.stop="removeUser(user)">
        Remove
      </button>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  props: {
    user: {
      type: Object,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
    isCurrent: {
      type: Boolean,
      required: true,
    },
  },
  computed: {
    ...mapState(["userEmail"]),
  },
  methods: {
    addUser(user) {
      this.$emit("addUser", user.email);
    },
    removeUser(user) {
      this.$emit("removeUser", user.email);
    },
  },
};
</script>
