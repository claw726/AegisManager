<template>
  <div class="flex flex-col">
    <OrgUserCard
      v-for="(user, index) in users"
      :key="index"
      :user="user"
      :index="index"
      :isCurrent="false"
      @addUser="addUser"
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
    addUser(email) {
      this.$emit("addUser", email);
    },
  },
};
</script>