import { triggerEvent } from "element-plus/es/utils";
import ACCESS_ENUM from "./accessEnum";

/**
 * 检查权限
 * @param loginUser 当前登录用户
 * @param needAccess 需要权限
 * @return boolean 有无权限
 */
const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  // 获取当前用户权限
  // 如果没有loginUser，权限为NOT_LOGIN
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;

  // 判断是否有权限
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess !== ACCESS_ENUM.NOT_LOGIN) {
      return true;
    }
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess === ACCESS_ENUM.ADMIN) {
      return true;
    }
  }
  return false;
};
export default checkAccess;
