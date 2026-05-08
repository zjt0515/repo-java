<template>
  <div class="userLoginView">
    <el-card class="login-card" shadow="hover">
      <h2 class="login-title">用户登录</h2>
      <el-form
        :model="form"
        label-width="auto"
        :rules="rules"
        ref="formRef"
        @submit.prevent="onSubmit"
      >
        <el-form-item label="账号" prop="userAccount">
          <el-input
            v-model="form.userAccount"
            placeholder="请输入账号"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input
            v-model="form.userPassword"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            class="login-btn"
            type="primary"
            size="large"
            @click="onSubmit"
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <el-link type="primary" @click="goToRegister">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { UserControllerService } from '../../../generated/services/UserControllerService';
import { reactive, ref } from 'vue'
import { useUserStore } from '@/store/user';
import { useRouter } from 'vue-router';

const userStore = useUserStore()
const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  userAccount: '',
  userPassword: '',
})

const rules = {
  userAccount: [
    { required: true, message: '请输入账号', trigger: 'blur' },
  ],
  userPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ],
}

const onSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await UserControllerService.userLoginUsingPost(form)
    console.log(res)
    if (res.code === 0) {
      await userStore.getLoginUser()
      router.push({ path: '/', replace: true })
      ElMessage.success('登录成功')
    } else {
      ElMessage.error('登录失败，' + res.message)
    }
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  router.push('/user/register')
}
</script>

<style scoped>
.userLoginView {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.login-card {
  width: 420px;
  border-radius: 12px;
}

.login-title {
  text-align: center;
  margin-bottom: 32px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
}
</style>