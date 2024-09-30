module.exports = {
    devServer: {
      proxy: {
        '/api': {
          target: 'https://localhost:8443',
          changeOrigin: true,
          pathRewrite: { '^/api': '' }
        }
      }
    }
  }