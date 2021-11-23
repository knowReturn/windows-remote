# Passer #
 欢迎来到我的主页，但是应该不会有任何东西<br>
喜欢干净

## windows-remote Windows远程桌面一个简单后台demo
 1. monitorserver：Windows远程桌面Java-springboot编写得后台代码，该项目使用robot执行键鼠操作<br>
    获取客户端桌面使用截图方式，截图方法：
    - 使用robot去获取当前屏幕截图，多屏也可以获取，具体需要自己修改此处相关代码
    - 使用Javacv-FFmpeg去获取当前屏幕信息，设定帧速率并抓帧，项目写有该方法，需要改写controller
    - 使用python，本项目下wsscreen.exe使用python去获取截图，并通过websocket推送，当前程序接口使用方式
    通过websocket与页面交互，通过localhost:8080/doc.html开启或者关闭监控
    
 2. remote-web远程桌面web页面
    该web项目用于展示客户端桌面和获取当前页面得键鼠操作：
    - 使用时需要修改views/Home.vue页面中web-socket标签中对应websocket链接ip
    - 使用npm install安装
    - npm run serve启动，访问页面即可连接到对应websocket
    - 在后台swagger页面开启监控刷新页面即可看到对应客户端电脑桌面
    - 部分组合键并不支持

### TODO
 demo比较粗糙，需要更好去完善：
  - 需要一个更加好得切实可行得思路，获取其他更好得方法改进获取相应客户端桌面信息。
  - 传输采用json或者字节方式，没有采用压缩方法，导致每次传输数据都比较大
  - 在web端有很多按键会有冲突

### QQ：2579437849
