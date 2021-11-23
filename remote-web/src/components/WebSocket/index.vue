<template>
  <div class="socket"></div>
</template>


<script>
  export default {
    props: ['socketData'],
    name: 'socket',
    data() {
      return {
        socket: null,
        path: this.socketData.path
      }
    },
    created() {
      console.log(this.socketData.path)  
      this.init();
    },
    methods: {        
        // websocket
        init: function() {
            if (typeof WebSocket === "undefined") {
                alert("您的浏览器不支持socket");
            } else {
                // 实例化socket
                this.socket = new WebSocket(this.path);
                // 监听socket连接
                this.socket.onopen = this.open;
                // 监听socket错误信息
                this.socket.onerror = this.error;
                // 监听socket消息
                this.socket.onmessage = this.getMessage;
            }
        },
        open: function(value) {
            console.log("全局websocket连接成功");
            
        },
        error: function(e) {
            console.log("全局websocket连接断开");
        },
        getMessage: function(msg) {
            if(msg.data === '{}'){
                console.log('socket没有传数据')
            }else{
                // const msgData = JSON.parse(msg.data)
                // console.log(msgData)
                this.socketFunc(msg.data)   
            }
        },
        send: function(value) {
            console.log("发送消息");
            this.socket.send(value)
        },
        //* 向父组件传值
        socketFunc(msgData) {
            this.$emit('func', msgData)
        }
    },
    destroyed() {
      console.log('离开页面，销毁组件，关闭全局websocket')
      //*离开路由之后断开websocket连接
      this.socket.close() 
    },
  }
</script>

<style>

</style>