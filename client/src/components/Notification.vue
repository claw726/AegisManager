<template>
 <div class="notification" :class="type">
   <div class="notification-content">     
     <span>{{ "Hello User!" + "\n" + message }}</span>
   </div>
   <button class="close-btn" @click="closeNotification">
     &times;
   </button>
 </div>
</template>


<script>
export default {




 props: {
   type: {
     type: String,
     default: 'info' // or success or error
   },


   message: {
     type: String,
     default: 'Please implement a custom message for this notification.'
   },
 },
 methods: {
   closeNotification() {
     this.$emit('close');
   }
 }
}
</script>


<style scoped>
.notification {
 position: fixed;
 top: 30px;
 right: 650px;
 text-align: center;
 padding: 15px 20px;
 border-radius: 5px;
 box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
 z-index: 1000;
}


.notification-content {
 display: flex;
 align-items: center;
 white-space: pre-wrap;
}


.close-btn {
 background: none;
 border: none;
 cursor: pointer;
 font-size: 18px;
 margin-left: 10px;
}


/* Add styles for different types */
.info {
 background-color: #e0f3fe;
 color: #10426b;
}


.success {
 background-color: #c8e6c9;
 color: #255c27;
}


.error {
 background-color: #ffcdd2;
 color: #f44336;
}


/* Add more styles as needed */
</style>