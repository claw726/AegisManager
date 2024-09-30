console.log('Using vue.config.js for proxy configuration');

module.exports = {
  devServer: {
    proxy: 'https://localhost:8443',
  },
};