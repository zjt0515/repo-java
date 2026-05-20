package com.zjt.ojserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjt.ojcommon.common.ErrorCode;
import com.zjt.ojcommon.exception.BusinessException;
import com.zjt.ojmodel.model.dto.user.UserQueryRequest;
import com.zjt.ojmodel.model.entity.User;
import com.zjt.ojmodel.model.enums.UserRoleEnum;
import com.zjt.ojmodel.model.vo.LoginUserVO;
import com.zjt.ojmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.zjt.ojcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 服务间共享
 * 用户服务
 */
@FeignClient(name = "oj-user-service", path = "/api/user/inner")
public interface UserFeignClient {
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}
