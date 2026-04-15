import { createApp } from "vue";
import App from "./App.vue";
import { Button, NavBar } from "vant";

const app = createApp(App);

// 在app上全局注册组件
app.use(Button);
app.use(NavBar);
app.mount("#app");
