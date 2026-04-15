import ACCESS_ENUM from "@/access/accessEnum";
import router from "@/router";
import { useUserStore } from "@/store/user.ts";
import checkAccess from "./checkAccess";
import { ElMessage } from "element-plus";

// 路由
router.beforeEach(async (to, from) => {
  console.log(useUserStore().loginUser);
  let loginUser = useUserStore().loginUser;
  // (这里是尝试自动登录)如果未登录，尝试登录
  if (!loginUser || !loginUser.userRole) {
    await useUserStore().getLoginUser();
    // 登录之后必须重新获取局部变量loginUser
    loginUser = useUserStore().loginUser;
  }
  const needAccess = to.meta?.access ?? ACCESS_ENUM.NOT_LOGIN;
  console.log(needAccess, loginUser.userRole);
  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    // console.log(needAccess, loginUser.userRole);
    // (这里是需要手动登录)没登陆, 这里区别于上面是因为自动登录调用后肯定会加上userROle
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole == ACCESS_ENUM.NOT_LOGIN
    ) {
      ElMessage("未登录!, 正在跳转到登录页面...");
      return `/user/login?redirect=${to.fullPath}`;
    }

    if (!checkAccess(loginUser, needAccess as string)) {
      // 没有权限就跳转到无权限页面|提示无权限
      alert("权限不足！");
      return false;
    } else {
      // 有权限直接进入
      return;
    }
  }
  return;
});
