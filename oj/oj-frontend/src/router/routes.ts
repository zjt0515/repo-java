import { RouteRecordRaw } from "vue-router";
import HomeView from "@/views/HomeView.vue";

import NoAuthView from "@/views/NoAuthView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import AddQuestionView from "@/views/question/AddQuestionView.vue";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/user/UserLoginView.vue";
import UserRegisterView from "@/views/user/UserRegisterView.vue";
import ManageQuestionView from "@/views/question/ManageQuestionView.vue";
import UpdateQuestionView from "@/views/question/UpdateQuestionView.vue";
import QuestionsView from "@/views/question/QuestionsView.vue";
import DoQuestionView from "@/views/question/DoQuestionView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    name: "用户",
    path: "/user",
    component: UserLayout,
    meta: {
      hideInMenu: true,
    },
    children: [
      {
        name: "登录",
        path: "/user/login",
        component: UserLoginView,
      },
      {
        name: "注册",
        path: "/user/register",
        component: UserRegisterView,
      },
    ],
  },
  {
    name: "主页",
    path: "/",
    component: HomeView,
  },
  {
    name: "题库",
    path: "/questions",
    component: QuestionsView,
  },
  // 题目路由
  {
    name: "创建题目",
    path: "/add/question",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    name: "管理题目",
    path: "/manage/question",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    name: "更新题目",
    path: "/update/question",
    component: UpdateQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
      hideInMenu: true,
    },
  },
  {
    name: "做题页面",
    path: "/question/:id",
    component: DoQuestionView,
    props: true,
    meta: {
      hideInMenu: true,
    },
  },
  {
    name: "无权限",
    path: "/noauth",
    component: NoAuthView,
    meta: {
      access: ACCESS_ENUM.NOT_LOGIN,
      hideInMenu: true,
    },
  },
];
