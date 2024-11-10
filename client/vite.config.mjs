// vite.config.js
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import { fileURLToPath } from "node:url";
import fs from "node:fs";

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
    environment: "happy-dom", // Use the jsdom environment for DOM-related tests
  },
  server: {
    port: 8443,
    host: "0.0.0.0",
    https: {
      key: fs.readFileSync("./keystore/key.pem"),
      cert: fs.readFileSync("./keystore/cert.pem"),
    },
    proxy: {
      "/api": {
        target: "https://localhost:8444", // Target server
        ws: true, // Enable WebSocket support
        changeOrigin: true, // Change the origin of the host header to the target URL
        secure: false,
        headers: {
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
          "Access-Control-Allow-Headers":
            "Content-Type, Authorization, X-Requested-With",
        },
      },
    },
    cors: {
      origin: ["https://localhost:8443", "https://0.0.0.0:8443"],
      methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
      allowedHeaders: ["Content-Type", "Authorization", "X-Requested-With"],
      credentials: true,
    },
  },
  define: {
    global: {},
  },
});
