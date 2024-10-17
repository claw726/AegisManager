import "@testing-library/jest-dom";

// jest.config.js
export default {
  preset: "@vue/cli-plugin-unit-jest",
  testEnvironment: "jsdom", // Use jsdom for DOM-related tests
  moduleFileExtensions: ["js", "json", "vue", "ts"],
  transform: {
    "^.+\\.vue$": "vue-jest",
    "^.+\\.js$": "babel-jest",
  },
  setupFilesAfterEnv: ["<rootDir>/jest.setup.js"], // Optional: for additional setup
};
