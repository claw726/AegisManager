// vite.config.js
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { fileURLToPath } from "node:url";

export default defineConfig({
  plugins: [
    vue({
      template: {
        compilerOptions: {
          isCustomElement: (tag) => ["Button"].includes(tag),
        },
      },
    }),
  ],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  test: {
    globals: true, // Enable global test functions like describe, it, beforeEach, etc.
    environment: "jsdom", // Use the jsdom environment for DOM-related tests
  },
  server: {
    port: 8081,
    proxy: {
      "/api": {
        target: "http://localhost:8080", // Target server
        ws: true, // Enable WebSocket support
        changeOrigin: true, // Change the origin of the host header to the target URL
      },
    },
  },
});
