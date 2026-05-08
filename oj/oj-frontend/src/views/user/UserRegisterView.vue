<template>
  <div class="userRegisterView">
    <el-card class="register-card" shadow="hover">
      <h2 class="register-title">用户注册</h2>
      <el-form
        :model="form"
        label-width="auto"
        :rules="rules"
        ref="formRef"
        @submit.prevent="handleSubmit"
      >
        <el-form-item label="账号" prop="userAccount">
          <el-input
            v-model="form.userAccount"
            placeholder="请输入账号"
            clearable
          />
        </el-form-item>
        <el-form-item label="密码" prop="userPassword">
          <el-input
            v-model="form.userPassword"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="checkPassword">
          <el-input
            v-model="form.checkPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            class="register-btn"
            type="primary"
            size="large"
            @click="handleSubmit"
            :loading="loading"
          >
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { UserControllerService, UserRegisterRequest } from "../../../generated";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const formRef = ref();
const loading = ref(false);

const form = reactive<UserRegisterRequest>({
  userAccount: "",
  userPassword: "",
  checkPassword: ""
});

const validatePass2 = (_rule: any, value: any, callback: any) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.userPassword) {
    callback(new Error("两次输入密码不一致"));
  } else {
    callback();
  }
};

const rules = {
  userAccount: [
    { required: true, message: "请输入账号", trigger: "blur" },
    { min: 4, message: "账号长度至少4位", trigger: "blur" },
  ],
  userPassword: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 8, message: "密码长度至少8位", trigger: "blur" },
  ],
  checkPassword: [
    { required: true, validator: validatePass2, trigger: "blur" },
  ],
};

const router = useRouter();

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false);
  if (!valid) return;

  loading.value = true;
  try {
    const res = await UserControllerService.userRegisterUsingPost(form);
    if (res.code === 0) {
      router.push({
        path: "/user/login",
        replace: true,
      });
      ElMessage.success("注册成功，请登录");
    } else {
      ElMessage.error("注册失败，" + res.message);
    }
  } finally {
    loading.value = false;
  }
};

const goToLogin = () => {
  router.push("/user/login");
};
</script>

<style scoped>
.userRegisterView {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 20px;
}

.register-card {
  width: 420px;
  border-radius: 12px;
}

.register-title {
  text-align: center;
  margin-bottom: 32px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.register-btn {
  width: 100%;
  margin-top: 8px;
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
}
</style>
