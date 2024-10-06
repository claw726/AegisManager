import { createApp } from 'vue'
import App from './App.vue'
import router from './routes.js'
import './styles/tailwind.css';
import $bus from './utils/events.js'
import store from './store/index.js'
import { addBeforeUnloadListener, removeBeforeUnloadListener } from './utils/autoLogout.js';

const app = createApp(App)

app.use(router);

app.use(store);

app.config.globalProperties.$bus = $bus;

  
app.mount('#app')

// Add the beforeunload event listener
addBeforeUnloadListener();

WebSocket.onClose = () => {
    console.log('WebSocket connection closed');
}
