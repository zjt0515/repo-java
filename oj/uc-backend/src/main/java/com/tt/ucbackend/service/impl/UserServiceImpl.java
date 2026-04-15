package com.tt.ucbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tt.ucbackend.common.BaseResponse;
import com.tt.ucbackend.model.domain.User;
import com.tt.ucbackend.service.UserService;
import com.tt.ucbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tt.ucbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author genshinya
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-09-10 18:58:34
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SPECIAL_CHARACTERS_PATTERN = "[^\\w\\s]"; // 正则表达式，匹配非字母、数字、下划线和空格的字符
    private static final String SALT = "test";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String studentNumber) {
        // 非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, studentNumber)) {
            return -1;
        }
        // 位数校验
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        if (studentNumber.length() != 9) {
            return -1;
        }
        // 账户校验特殊字符
        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_PATTERN);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码是否相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 学号校验
        Pattern stuNumPattern = Pattern.compile("[Bb]\\d{8}");
        Matcher stuNumMatcher = stuNumPattern.matcher(studentNumber);
        if (!stuNumMatcher.find()){
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -1;
        }
        // 学号不重复
        QueryWrapper<User> stuNumQueryWrapper = new QueryWrapper<>();
        stuNumQueryWrapper.eq("student_number", studentNumber);
        count = userMapper.selectCount(stuNumQueryWrapper);
        if (count > 0) {
            return -1;
        }
        // 密码加密
        // MessageDigest md5 = MessageDigest.getInstance("MD5");

        String encryptPassword = encryptPassword(userPassword);

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setStudentNumber(studentNumber);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getUuid();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        // 位数校验
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户校验特殊字符
        Pattern pattern = Pattern.compile(SPECIAL_CHARACTERS_PATTERN);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_password", encryptPassword(userPassword));
        queryWrapper.eq("user_account", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("oh shit...user login failed");
            return null;
        }
        User safeUser = getSafeUser(user);

        // 记录用户登录态，这里user可以优化，同时设置超时时间
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
         // 限流，登录次数过多限制登录
        return safeUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (attribute == null){
            return false;
        } else {
            request.getSession().removeAttribute(USER_LOGIN_STATE);
        }
        return true;
    }

    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }


    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user) {
        if (user == null) {
            return null;
        }
        // 返回脱敏用户
        User safeUser = new User();
        safeUser.setUuid(user.getUuid());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        return safeUser;
    }
}




