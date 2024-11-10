import axios from "axios";

const instance = axios.create({
  // No baseURL here, so requests will be relative and use the proxy
});

export default instance;
