<template>
  <div class="basicLayout">
    <el-container style="min-height: 120vh;">
      <el-header class="header">
        userheader
      </el-header>
      <el-main class="context">
        <RouterView />
        <router-link to="/about">链接</router-link>
      </el-main>
      <el-footer class=" footer">Footer</el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
</script>


<style scoped>
.header {
  box-shadow: #eee, 1px, 1px, 5px;
}

.context {
  padding: 20px;
  background: linear-gradient(to right, #eee, #fff);
}

.footer {
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  background: #1A1A1A;
  text-align: center;
}
</style>
