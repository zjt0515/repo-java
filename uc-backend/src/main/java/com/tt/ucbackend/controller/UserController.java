package com.tt.ucbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tt.ucbackend.common.BaseResponse;
import com.tt.ucbackend.common.ErrorCode;
import com.tt.ucbackend.common.ResultUtils;
import com.tt.ucbackend.model.domain.User;
import com.tt.ucbackend.model.domain.request.UserLoginRequest;
import com.tt.ucbackend.model.domain.request.UserRegisterRequest;
import com.tt.ucbackend.service.UserService;
import com.tt.ucbackend.service.impl.UserServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tt.ucbackend.constant.UserConstant.ADMIN_ROLE;
import static com.tt.ucbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author: tt
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String studentNumber = userRegisterRequest.getStudentNumber();
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentNumber)) return null;

        long l = userService.userRegister(userAccount, userPassword, checkPassword, studentNumber);
        return ResultUtils.success(l);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest){
        if (userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) return null;

        User user = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(user);
    }

    @GetMapping("/current")
    public BaseResponse<User> currentUser(HttpServletRequest request){
        if (request == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) attribute;
        if (user == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        User currentUser = userService.getById(user.getUuid());
        User safeUser = userService.getSafeUser(currentUser);
        return ResultUtils.success(safeUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String userName, HttpServletRequest request){
        if (!isAdmin(request)){
            return ResultUtils.error(ErrorCode.NOT_LOGIN);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // todo：写到userService中
        if (StringUtils.isNotBlank(userName)){
            queryWrapper.like("user_name", userName);
        }
        // 用户脱敏
        List<User> list = userService.list(queryWrapper).stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        // 鉴权：管理员
        if (!isAdmin(request)){
            return ResultUtils.error(ErrorCode.NO_AUTH);
        }
        if (id <= 0){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        Boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        if (request == null) {
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        boolean result = userService.userLogout(request);
        if (result) {
            return ResultUtils.success(true);
        } else {
            return ResultUtils.error(ERR);
        }
    }

    /**
     * 是否为管理
     * @param request 请求
     * @return 是否管理
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null){
            return false;
        }
        if (user.getUserRole() != ADMIN_ROLE){
            return false;
        } else {
            return true;
        }
    }


}
