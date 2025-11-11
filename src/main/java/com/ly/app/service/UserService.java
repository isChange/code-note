package com.ly.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.app.domain.entity.User;
import com.ly.app.domain.dto.user.UserCreateRequest;
import com.ly.app.domain.dto.user.UserUpdateRequest;
import com.ly.app.domain.vo.user.SafeUserVO;

/**
* @author admin
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-05-30 18:18:33
*/
public interface UserService extends IService<User> {
    /**
     *
     * @param account
     * @param password
     * @param checkPassword
     * @return 注册账户
     */
    Long registerUser(String account, String password, String checkPassword);
    /**
     *
     * @param account
     * @param password
     * @return 登录账户
     */
    SafeUserVO loginUser (String account, String password);
    /**
     *
     * @return 登出账户
     */
    Boolean loginOutUser();
    /**
     *
     * @param user
     * @return 创建用户
     */
    Long createUser(UserCreateRequest user);
    /**
     *
     * @param user
     * @return 修改用户
     */
    Boolean updateUser(UserUpdateRequest user);
    /**
     *
     * @param id
     * @return 删除用户
     */
    Boolean deleteUser(Long id);
    /**
     *
     * @param id
     * @param account
     * @return 查询用户
     */
    SafeUserVO getUser(Long id, String account);

    /**
     * 判断用户是否是管理员
     * @param user
     * @return
     */
    Boolean isAdmin(SafeUserVO user);
    /**
     * 获取当前登录用户
     * @return
     */
    SafeUserVO getLoginUser();
}
