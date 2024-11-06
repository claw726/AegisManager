/** @type {import{'tailwindcss'}.Config} */
export default {
  theme: {
    extend: {
      colors: {
        primary: "#202D10",
        secondary: "#0D203B",
        accent: "#704F32",
        background: "#f3f3f3",
        highlight: "#9DB3C1",
      },
    },
  },
  content: ["./index.html", "./src/**/*.{ts,tsx,js,jsx,vue}"],
  plugins: [],
};
