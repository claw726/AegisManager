<template>
    <div class="relative w-full h-screen bg-white">
      <NavBar />

    <!-- Kanban Board -->
    <div class="absolute left-28 top-96 flex space-x-8">
      <div class="flex flex-col w-72">
        <div class="flex items-center justify-between p-4 bg-white border border-gray-300 rounded-t-lg">
          <div class="text-xl font-bold text-gray-800">Column Title</div>
          <div class="w-6 h-6 border border-gray-300 rounded-full flex items-center justify-center">
            <span class="text-sm font-bold text-gray-800">0</span>
          </div>
        </div>
        <div class="flex flex-col space-y-4 p-4 bg-white border border-gray-300 rounded-b-lg">
          <div class="p-4 bg-white border border-gray-300 rounded-lg">Task 1</div>
          <div class="p-4 bg-white border border-gray-300 rounded-lg">Task 2</div>
        </div>
      </div>
      <!-- Repeat for additional columns -->
    </div>
    </div>
</template>