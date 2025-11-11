package com.ly.app.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.app.common.constant.UserConstant;
import com.ly.app.common.enums.error.AssertEnum;
import com.ly.app.common.enums.error.UserEnum;
import com.ly.app.domain.enums.UserRoleEnum;
import com.ly.app.common.enums.error.ErrorCode;
import com.ly.app.common.units.AssertUtil;
import com.ly.app.common.units.DateUtil;
import com.ly.app.common.units.PasswordUtil;
import com.ly.app.domain.entity.User;
import com.ly.app.domain.dto.user.UserCreateRequest;
import com.ly.app.domain.dto.user.UserUpdateRequest;
import com.ly.app.domain.cover.UserCovert;
import com.ly.app.domain.vo.user.SafeUserVO;
import com.ly.app.mapper.UserMapper;
import com.ly.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author liu yi
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    /**
     * 注册用户
     */
    @Override
    public Long registerUser(String account, String password, String checkPassword) {
        // 1. 校验参数是否合法
        AssertUtil.isNotNull(AssertEnum.PARAMS_EMPTY, account, password, checkPassword);
        AssertUtil.isTrue(password.length() >= 6 && password.length() <= 16, UserEnum.USER_PASSWORD_INVALID);
        AssertUtil.isTrue(password.equals(checkPassword), UserEnum.USER_PASSWORD_NOT_EQUAL);

        // 2. 校验账号是否已存在
        User existUser = this.lambdaQuery().eq(User::getUserAccount, account).one();
        AssertUtil.isNull(existUser, UserEnum.USER_EXIST);

        // 3. 密码加密（使用BCrypt，更安全的方式）
        LocalDateTime now = LocalDateTime.now();
        String encryptPassword = PasswordUtil.encryptBCrypt(password);

        // 4. 创建用户
        User user = new User();
        user.setUserAccount(account);
        user.setCreateTime(now);
        user.setUserPassword(encryptPassword);
        user.setUserRole(UserConstant.DEFAULT_ROLE);

        AssertUtil.isTrue(this.save(user), ErrorCode.OPERATION_ERROR);
        log.info("用户注册成功，账号: {}", account);
        return user.getId();
    }

    /**
     * 用户登录
     */
    @Override
    public SafeUserVO loginUser(String account, String password) {
        // 1. 校验参数
        AssertUtil.isNotNull(AssertEnum.PARAMS_EMPTY, account, password);
        AssertUtil.isTrue(password.length() >= 6 && password.length() <= 16, UserEnum.USER_PASSWORD_INVALID);

        // 2. 查询用户
        User user = this.lambdaQuery().eq(User::getUserAccount, account).one();
        AssertUtil.isNotNull(user, UserEnum.USER_LOGIN_FAIL);

        // 3. 校验密码（使用BCrypt验证）
        AssertUtil.isTrue(PasswordUtil.checkBCrypt(password, user.getUserPassword()), UserEnum.USER_LOGIN_FAIL);

        // 4. 登录成功，使用 SaToken 进行会话管理
        SafeUserVO safeUser = UserCovert.INSTANCE.userToSafeUserVO(user);

        // 使用 SaToken 登录，将用户 ID 作为登录标识
        StpUtil.login(user.getId());

        // 将用户信息存储到 SaToken Session 中
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATUS, safeUser);

        log.info("用户登录成功，账号: {}, 用户ID: {}, Token: {}", account, user.getId(), StpUtil.getTokenValue());
        return safeUser;
    }

    /**
     * 用户登出
     */
    @Override
    public Boolean loginOutUser() {
        // 检查是否已登录
        AssertUtil.isTrue(StpUtil.isLogin(), ErrorCode.NOT_LOGIN_ERROR);

        Object loginId = StpUtil.getLoginId();

        // SaToken 登出
        StpUtil.logout();

        log.info("用户登出成功，用户ID: {}", loginId);
        return true;
    }

    /**
     * 创建用户（管理员）
     */
    @Override
    public Long createUser(UserCreateRequest request) {
        AssertUtil.isNotNull(request, AssertEnum.PARAMS_EMPTY);
        String account = request.getUserAccount();
        String password = request.getUserPassword();

        // 1. 校验参数
        AssertUtil.isNotNull(AssertEnum.PARAMS_EMPTY, account, password);
        AssertUtil.isTrue(password.length() >= 6 && password.length() <= 16, UserEnum.USER_PASSWORD_INVALID);

        // 2. 校验账号是否已存在
        User existUser = this.lambdaQuery().eq(User::getUserAccount, account).one();
        AssertUtil.isNull(existUser, UserEnum.USER_EXIST);

        // 3. 密码加密（使用BCrypt，更安全的方式）
        LocalDateTime now = LocalDateTime.now();
        String encryptPassword = PasswordUtil.encryptBCrypt(password);

        // 4. 创建用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setCreateTime(now);
        user.setUserPassword(encryptPassword);

        AssertUtil.isTrue(this.save(user), ErrorCode.OPERATION_ERROR);
        log.info("管理员创建用户成功，账号: {}", account);
        return user.getId();
    }

    /**
     * 更新用户信息
     */
    @Override
    public Boolean updateUser(UserUpdateRequest request) {
        // 1. 校验参数
        AssertUtil.isNotNull(request, AssertEnum.PARAMS_EMPTY);
        String account = request.getUserAccount();
        AssertUtil.isNotBlank(account, AssertEnum.PARAMS_EMPTY);

        // 2. 查询用户
        User user = this.lambdaQuery().eq(User::getUserAccount, account).one();
        AssertUtil.isNotNull(user, UserEnum.USER_NOT_EXIST);

        // 3. 更新用户信息
        user.setUserName(request.getUserName());
        user.setUserAvatar(request.getUserAvatar());
        user.setUserProfile(request.getUserProfile());
        user.setUpdateTime(LocalDateTime.now());

        AssertUtil.isTrue(this.updateById(user), ErrorCode.OPERATION_ERROR);

        // 4. 如果当前登录用户就是被更新的用户，同步更新 SaToken Session 中的信息
        if (StpUtil.isLogin()) {
            SafeUserVO loginUser = getLoginUser();
            if (loginUser != null && loginUser.getUserAccount().equals(account)) {
                SafeUserVO updatedUser = UserCovert.INSTANCE.userToSafeUserVO(user);
                StpUtil.getSession().set(UserConstant.USER_LOGIN_STATUS, updatedUser);
            }
        }

        log.info("用户信息更新成功，账号: {}", account);
        return true;
    }

    /**
     * 删除用户
     */
    @Override
    public Boolean deleteUser(Long id) {
        AssertUtil.isNotNull(id, AssertEnum.PARAMS_EMPTY);

        User user = this.getById(id);
        AssertUtil.isNotNull(user, UserEnum.USER_NOT_EXIST);

        AssertUtil.isTrue(this.removeById(id), ErrorCode.OPERATION_ERROR);
        log.info("用户删除成功，用户ID: {}", id);
        return true;
    }

    /**
     * 查询用户
     */
    @Override
    public SafeUserVO getUser(Long id, String account) {
        if (ObjectUtil.isAllEmpty(id, account)) {
            return null;
        }

        User user = this.lambdaQuery()
                .eq(ObjectUtil.isNotNull(id), User::getId, id)
                .eq(StringUtils.isNotBlank(account), User::getUserAccount, account)
                .one();

        return UserCovert.INSTANCE.userToSafeUserVO(user);
    }

    /**
     * 判断是否为管理员
     */
    @Override
    public Boolean isAdmin(SafeUserVO user) {
        return ObjectUtil.isNotNull(user) &&
               UserRoleEnum.ADMIN.equals(UserRoleEnum.getRoleByKey(user.getUserRole()));
    }

    /**
     * 获取当前登录用户
     */
    @Override
    public SafeUserVO getLoginUser() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            return null;
        }

        // 从 SaToken Session 中获取用户信息
        return (SafeUserVO) StpUtil.getSession().get(UserConstant.USER_LOGIN_STATUS);
    }
}




