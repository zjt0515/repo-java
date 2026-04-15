<template>
  <div class="userLoginView">
    <el-form :model="form" label-width="auto" style="max-width: 600px">
      <el-form-item label="账号">
        <el-input v-model="form.userAccount" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="form.userPassword" />

      </el-form-item>


      <el-form-item>
        <el-button type="primary" @click="onSubmit">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { UserControllerService } from '../../../generated/services/UserControllerService';
import { reactive } from 'vue'
import { useUserStore } from '@/store/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore()
const router = useRouter()

// do not use same name with ref
const form = reactive({
  userAccount: '',
  userPassword: '',
})

const onSubmit = async () => {
  // 调用后端接口
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code === 0) {
    // 前端从后端取数据，await：等成功获取数据后再跳转
    await userStore.getLoginUser();
    router.push({
      path: "/",
      replace: true
    })
    //alert("登录成功" + JSON.stringify(res.data));
  } else {
    ElMessage.error("登录失败，" + res.message);
  }

}
</script>