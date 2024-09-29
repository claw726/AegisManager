import axios from 'axios';

const instance = axios.create({
  baseURL: 'https://192.168.0.77:8443', //Change to your machines IP address on the LAN
});

export default instance;