import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "./../../generated/services/UserControllerService";
import { defineStore } from "pinia";

interface LoginUser {
  userName?: string;
  id: string;
  userRole?: string;
}

export const useUserStore = defineStore("user", {
  // state 必须是一个返回对象的函数
  state: () => ({
    loginUser: {
      userName: "未登录",
      id: "0",
      //userRole: ACCESS_ENUM.NOT_LOGIN, 未登录没有userRole，以便于区分登录失败和未登录
    } as LoginUser,
  }),

  // getters
  getters: {
    getUser: (state) => state.loginUser,
  },
  /*
    {
    "code": 0,
    "data": {
      "id": "1734136842169933826",
      "userName": "genshinya",
      "userAvatar": null,
      "userProfile": null,
      "userRole": "user",
      "createTime": "2023-12-11T09:03:36.000+00:00",
      "updateTime": "2023-12-11T14:31:22.000+00:00"
    },
    "message": "ok"
  }
  */
  actions: {
    /**
     * 用户自动登录
     */
    async getLoginUser() {
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code == 0) {
        // 更新 userStore
        console.log(res.data);
        this.updateUser(res.data);
      } else {
        this.loginUser = {
          ...this.loginUser,
          userRole: ACCESS_ENUM.NOT_LOGIN,
        };
      }
    },
    updateUser(data: any) {
      const { userName, id, userRole } = data;
      this.loginUser = {
        userName,
        id,
        userRole
      };
    },
  },
});
