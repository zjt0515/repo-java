<template>
  <div>
    <el-row id="globalHeader" align="middle" :gutter="20">
      <el-col :span="4">
        <div class="grid-content ep-bg-purple" />
      </el-col>
      <el-col :span="16">
        <div class="grid-content ep-bg-purple">
          <!-- 菜单 -->
          <el-menu :default-active="activeIndex" mode="horizontal" :router=true>
            <!-- 动态显示路由 -->
            <el-menu-item v-for="item in accessRoutes" :key="item.path" :index="item.path">
              {{ item.name }}
            </el-menu-item>
          </el-menu>
          <div class="h-6" />
        </div>
      </el-col>
      <el-col :span="4">
        <div class="grid-content ep-bg-purple">
          {{ userStore.loginUser?.userName ?? "未登录" }}

        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts" name="GlobalHeader">
import { useUserStore } from '../store/user';
import { computed, ref } from 'vue';
import { routes } from '../router/routes';
import { useRouter } from 'vue-router';
import checkAccess from '../access/checkAccess'
import ACCESS_ENUM from '../access/accessEnum';

const router = useRouter()
// 监听路由变化，响应式渲染聚焦路由
const activeIndex = ref('/')
router.afterEach(to => {
  activeIndex.value = to.path
})
const userStore = useUserStore()

userStore.getLoginUser()
// 3s后登录
// setTimeout(() => {
//   userStore.updateUser({
//     userName: "管理员",
//     id: "Aelx",
//     userRole: ACCESS_ENUM.ADMIN
//   })
// }, 3000)
// 使用计算属性，根据权限变化响应式渲染路由显示
const accessRoutes = computed(() => {
  // 根据权限过滤路由数组
  return routes.filter(item => {
    if (item.meta?.hideInMenu === true) {
      return false;
    }
    if (checkAccess(userStore.loginUser, item?.meta?.access as any)) {
      return true;
    }
    return false;
  })

})


</script>

<style>
.el-row {
  margin-bottom: 20px;
}

.el-col {
  border-radius: 4px;
}
</style>