<template>
  <div class="flex flex-col">
    <OrgUserCard
      v-for="(user, index) in users"
      :key="index"
      :user="user"
      :index="index"
      :isCurrent="true"
      @removeUser="removeUser"
    />
  </div>
</template>

<script>
import OrgUserCard from "@/components/OrgUserCard.vue";

export default {
  components: {
    OrgUserCard,
  },
  props: {
    users: {
      type: Array,
      required: true,
    },
  },
  methods: {
    removeUser(email) {
      this.$emit("removeUser", email);
    },
  },
};
</script>
