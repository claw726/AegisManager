<!-- OrganizationCard.vue -->
  <template>
    <div class="flex flex-row justify-between p-4">
      <img :src="organization.OrgLogo" alt="Organization Logo" class="w-12 h-12 rounded-full drop-shadow-xl mr-4" />
      <div class="text-lg font-bold text-gray-800 text-center underline">{{ organization.OrgName }}</div>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      organization: {
        type: Object,
        required: true,
      },
    },
  };
  </script>