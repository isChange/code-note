package com.ly.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.app.common.annotation.ApiLog;
import com.ly.app.common.constant.UserConstant;
import com.ly.app.common.model.Result;
import com.ly.app.domain.entity.User;
import com.ly.app.domain.cover.UserCovert;
import com.ly.app.domain.dto.user.UserCreateRequest;
import com.ly.app.domain.dto.user.UserQueryRequest;
import com.ly.app.domain.dto.user.UserRegisterRequest;
import com.ly.app.domain.dto.user.UserUpdateRequest;
import com.ly.app.domain.vo.user.SafeUserVO;
import com.ly.app.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author liu yi
 */
@Api(tags = "用户模块")
@Slf4j
@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiLog(module = "用户管理", description = "注册用户")
    @ApiOperation("注册用户")
    @PostMapping("/register")
    public Result<Long> registerUser(@Validated @RequestBody UserRegisterRequest user) {
        Long id = userService.registerUser(user.getAccount(), user.getPassword(), user.getCheckPassword());
        return Result.success(id);
    }

    @ApiLog(module = "用户管理", description = "用户登入")
    @ApiOperation("登入")
    @PostMapping("/login")
    public Result<SafeUserVO> loginUser(String account, String password) {
        SafeUserVO safeUserVo = userService.loginUser(account, password);
        return Result.success(safeUserVo);
    }

    @ApiOperation("返回当前用户信息")
    @GetMapping("/get/login")
    public Result<SafeUserVO> getLoginUser() {
        return Result.success(userService.getLoginUser());
    }
    @ApiLog(module = "用户管理", description = "用户登出")
    @ApiOperation("登出")
    @GetMapping("/logout")
    public Result<Boolean> logoutUser() {
        Boolean logout = userService.loginOutUser();
        return Result.success(logout);
    }

    @ApiLog(module = "用户管理", description = "创建用户")
    @ApiOperation("创建用户")
    @PostMapping("/create")
    public Result<Long> createUser(@Validated @RequestBody UserCreateRequest user) {
        Long id = userService.createUser(user);
        return Result.success(id);
    }

    @ApiLog(module = "用户管理", description = "更新用户信息")
    @ApiOperation("更新用户信息")
    @PutMapping("/update")
    public Result<Boolean> updateUser(@Validated @RequestBody UserUpdateRequest user) {
        Boolean update = userService.updateUser(user);
        return Result.success(update);
    }

    @ApiLog(module = "用户管理", description = "删除用户")
    @ApiOperation("注销用户")
    @DeleteMapping("/delete")
    public Result<Boolean> deleteUser(Long id) {
        Boolean delete = userService.deleteUser(id);
        return Result.success(delete);
    }

    @ApiOperation("查询用户列表")
    @GetMapping("/page")
    public Result<Page<SafeUserVO>> page(UserQueryRequest user) {
        Page<User> page = userService.lambdaQuery()
                .eq(StringUtils.isNotBlank(user.getUserAccount()), User::getUserAccount, user.getUserAccount())
                .likeRight(StringUtils.isNotBlank(user.getUserName()), User::getUserName, user.getUserName())
                .likeRight(StringUtils.isNotBlank(user.getUserProfile()), User::getUserProfile, user.getUserProfile())
                .eq(StringUtils.isNotBlank(user.getUserRole()), User::getUserRole, user.getUserRole())
                .page(new Page<>(user.getCurrent(), user.getPageSize()));
         return Result.success(page.convert(UserCovert.INSTANCE::userToSafeUserVO));
    }

    @ApiOperation("查询用户")
    @GetMapping("/one")
     public Result<SafeUserVO> getUser(Long id, String account) {
        return Result.success(userService.getUser(id, account));
    }

}
