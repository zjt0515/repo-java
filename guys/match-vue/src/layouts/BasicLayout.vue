<template>
  <van-nav-bar title="标题" right-text="按钮" left-arrow @click-left="onClickLeft" @click-right="onClickRight">
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>
  <!-- 内容 -->
  <div id="content">
    <template v-if="active === 'index'">
      <Index />
    </template>
    <template v-if="active === 'team'">
      <Team />
    </template>
    <template v-if="active === 'user'">
      <User />
    </template>
  </div>

  <van-tabbar v-model="active" @change="onChange">
    <van-tabbar-item icon="home-o" name="index">主页</van-tabbar-item>
    <van-tabbar-item icon="search" name="team">队伍</van-tabbar-item>
    <van-tabbar-item icon="friends-o" name="user">个人</van-tabbar-item>
  </van-tabbar>

</template>

<script lang="ts" setup>
import { showToast } from 'vant';
import { ref } from 'vue';
import Index from '../pages/Index.vue';
import Team from '../pages/Team.vue';
import User from '../pages/User.vue';
const onClickLeft = () => alert("left测");
const onClickRight = () => alert("右测");

const active = ref("index");
const onChange = (index: String) => showToast(`${index}`);
</script>

<style scoped></style>