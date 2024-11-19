import globals from "globals";
import js from "@eslint/js";
import eslintPluginVue from "eslint-plugin-vue";

export default [
  {
    files: ["**/*.vue"],
    languageOptions: {
      parser: "vue-eslint-parser",
      parserOptions: {
        ecmaVersion: "latest",
        sourceType: "module",
        ecmaFeatures: {
          jsx: true,
        },
        parser: {
          js: "@babel/eslint-parser",
          ts: "@typescript-eslint/parser",
          "<template>": "vue-eslint-parser",
        },
      },
      globals: {
        ...globals.browser,
        ...globals.es2021,
      },
    },
    plugins: {
      vue: eslintPluginVue,
    },
    rules: {
      ...eslintPluginVue.configs.base.rules,
      ...eslintPluginVue.configs["vue3-recommended"].rules,
    },
  },
  {
    files: ["**/*.{js,mjs,cjs}"],
    languageOptions: {
      globals: {
        ...globals.browser,
        ...globals.es2021,
      },
    },
    plugins: {
      vue: eslintPluginVue,
    },
    rules: {
      ...js.configs.recommended.rules,
    },
  },
];
