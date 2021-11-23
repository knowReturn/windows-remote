<template>
  <div class="home" @mousewheel.prevent="" @mousedown.prevent="" @mouseup.prevent="" @contextmenu.prevent="" @keyup.prevent="" @keypress.prevent="" @keydown.prevent="">
        <img :src="url" alt="" style="width: 1920px; height: 1080px" @mousemove.prevent="mouseMove" @mousedown.prevent="mouseDown" @mouseup.prevent="mouseUp" @mousewheel.prevent="mouseWheel"/>
        <web-socket ref="socketOne" :socketData="{path: 'ws://192.168.101.9:5000'}" @func="getMessageData"/>
        <web-socket ref="socketTwo" :socketData="{path: 'ws://192.168.101.9:8888/myWs/screen'}" />
  </div>
</template>

<script>
import WebSocket from '../components/WebSocket/index'

export default {
  name: "Home",
    components: {WebSocket},
    data() {
        return {
            imgData: '',
            url: ''
        }
    },
    created() {
      // 监听键盘点击
      window.onkeydown = (event) => {
        console.log("按下" + ":" + event.keyCode)
        this.$refs.socketTwo.send(JSON.stringify({values: event.keyCode, type: "KEY_PRESS"}));
        event.keyCode = 0;
        event.returnvalue = false;
        window.event.preventDefault;
      },
      //监听键盘释放
      window.onkeyup = (event) => {
          console.log("释放" + ":" + event.keyCode)
          this.$refs.socketTwo.send(JSON.stringify({values: event.keyCode, type: "KEY_RELEASE"}))
          keyValue = -1
      }
    },
    methods: {
    // 监听鼠标移动
        mouseMove(event) {
          //console.log(event)
          this.$refs.socketTwo.send(JSON.stringify({values: event.pageX+','+event.pageY, type: "MOUSE_MOVE"}))
        },
    //监听鼠标按下
        mouseDown(event) {
            //console.log(event.which);
            this.$refs.socketTwo.send(JSON.stringify({values: event.which, type: "MOUSE_PRESS"}))
        },
    //监听鼠标释放事件
        mouseUp(event) {
            //console.log(event.which);
            this.$refs.socketTwo.send(JSON.stringify({values: event.which, type: "MOUSE_RELEASE"}))
        },
    //监听鼠标滑轮事件
        mouseWheel(event) {
            //console.log(event);
            var value = event.deltaY > 0 ? 1 : -1
            //console.log(JSON.stringify({values: values, type: "MOUSE_WHEEL"}))
            this.$refs.socketTwo.send(JSON.stringify({values: event.offsetX + ',' + event.offsetY + ',' + value, type: "MOUSE_WHEEL"}))
        },
        getImg() {
            return `data:image/jpeg;base64,${this.imgData}`
        },
        getMessageData(params) {
            this.url = window.URL.createObjectURL(params)
            this.imgData = params
        }
    }
};

//禁用键盘事件
document.onkeydown = function() {
    event.keyCode = 0;
    event.returnvalue = false;
    window.event.preventDefault;
}
</script>