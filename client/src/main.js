import { createApp } from "vue";
import App from "./App.vue";
import router from "./routes.js";
import "./styles/tailwind.css";
import './assets/main.css';
import store from "./store/index.js";
import { addBeforeUnloadListener } from "./utils/autoLogout.js";
import "@fortawesome/fontawesome-free/css/all.css";

const app = createApp(App);

app.use(router);

app.use(store);

app.mount("#app");

// Add the beforeunload event listener
addBeforeUnloadListener();

WebSocket.onClose = () => {
  console.log("WebSocket connection closed");
};
