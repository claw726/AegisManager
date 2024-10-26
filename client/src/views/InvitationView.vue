<template>
  <div class="relative w-full min-h-screen h-full bg-background">
    <!-- Navbar -->
    <NavBar />

    <!-- Invitations List -->
    <div class="flex justify-center items-center h-full px-28">
      <div class="w-full max-w-6xl">
        <h1 class="text-5xl font-extrabold text-primary mb-12">Invitations</h1>

        <div
          v-for="invitation in invitations"
          :key="invitation.id"
          class="border border-gray-300 rounded-lg shadow-lg p-8 mb-12"
        >
          <!-- Invitation Details -->
          <div class="flex justify-between items-center">
            <div>
              <p class="text-2xl font-semibold text-gray-900">
                {{ invitation.source }}
              </p>
              <p class="text-lg text-gray-700">
                From: {{ invitation.fromUser }}
              </p>
            </div>

            <!-- Icons: Accept and Decline -->
            <div class="flex space-x-6">
              <button
                @click="acceptInvitation(invitation.id)"
                class="p-3 bg-green-600 text-white rounded-full"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M5 13l4 4L19 7"
                  />
                </svg>
              </button>

              <button
                @click="declineInvitation(invitation.id)"
                class="p-3 bg-brown-600 text-white rounded-full"
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-6 w-6"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from "../components/NavBar.vue";

export default {
  name: "InvitationsPage",
  components: {
    NavBar,
  },
  data() {
    return {
      invitations: [
        {
          id: "inv001",
          source: "You have been added to Project 1",
          fromUser: "John Doe",
        },
        {
          id: "inv002",
          source: "You have been added to Task 8",
          fromUser: "Alice Smith",
        },
      ],
    };
  },
  methods: {
    acceptInvitation(id) {
      alert(`Invitation ${id} accepted.`);
    },
    declineInvitation(id) {
      alert(`Invitation ${id} declined.`);
    },
  },
};
</script>

<style scoped>
.bg-background {
  background-color: #f9fafb;
}

.text-primary {
  color: #1f2937;
}

.shadow-lg {
  box-shadow:
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.border {
  border-width: 1px;
}

.mb-12 {
  margin-bottom: 50px;
}

.bg-green-600 {
  background-color: #08471f;
}

.bg-brown-600 {
  background-color: #7b341e;
}

button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

svg {
  width: 24px;
  height: 24px;
}
</style>
