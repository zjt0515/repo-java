package com.genshinya.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.genshinya.usercenter.model.domain.User;
import com.genshinya.usercenter.service.UserService;
import com.genshinya.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.genshinya.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author genshinya
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-05-03 22:27:52
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "mySalt";

    /**
     * 用户登录态 key
     */

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            // todo 修改为自定义异常类
            return -1;
        }
        // 位数
        if (userPassword.length() < 6 || userAccount.length() < 8) {
            return -1;
        }
        // 密码和校验密码
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        // 账号不重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) return -1;

        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        log.info("注册成功：" + user.getUserAccount());
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        // 位数
        if (userPassword.length() < 6 || userAccount.length() < 8) {
            return null;
        }
        // 特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        // 查询userAccount是否存在，加密密码是否相等
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error("user login failed, userAccount cant mach password");
            return null;
        }
        // todo 限流

        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);

        // 4.记录用户的登录态
        // todo: 偷懒(直接将user作为value
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        log.info("登录成功: " + safetyUser.getUserAccount());
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param user 原用户
     * @return 脱敏用户
     */
    @Override
    public User getSafetyUser(User user){
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 删除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




