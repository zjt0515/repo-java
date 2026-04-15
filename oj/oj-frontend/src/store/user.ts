import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "./../../generated/services/UserControllerService";
import { defineStore } from "pinia";

interface loginUser {
  userName: string;
  id: string;
  userRole?: string;
}

export const useUserStore = defineStore("user", {
  state: () => ({
    loginUser: {
      userName: "未登录",
      id: "steve",
      //userRole: ACCESS_ENUM.NOT_LOGIN, 未登录没有userRole，以便于区分登录失败和未登录
    } as loginUser,
  }),
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
    async getLoginUser() {
      const res = await UserControllerService.getLoginUserUsingGet();
      if (res.code == 0) {
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
      this.loginUser = data;
    },
  },
});
