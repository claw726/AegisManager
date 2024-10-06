module.exports = {
    devServer: {
      proxy: {
        '/api/*': {
          target: 'http://localhost:8080',
          ws: true, // Enable WebSocket support
          changeOrigin: true,
        },
      },
    },
  };