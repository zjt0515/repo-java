<template>
  <div class="basicLayout">
    <el-container style="min-height: 120vh;">
      <el-header class="header">
        <GlobalHeader />
      </el-header>
      <el-main class="context">
        <RouterView />
      </el-main>
      <el-footer class=" footer">Footer</el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts" name="BasicLayout">
import GlobalHeader from '../components/GlobalHeader.vue';
</script>


<style scoped>
.header {
  box-shadow: #eee, 1px, 1px, 5px;
}

.context {
  padding: 20px;
  background: linear-gradient(to right, #efefef, #fff);
}

.footer {
  position: static;
  bottom: 0;
  left: 0;
  right: 0;
  background: #1A1A1A;
  text-align: center;
}
</style>
