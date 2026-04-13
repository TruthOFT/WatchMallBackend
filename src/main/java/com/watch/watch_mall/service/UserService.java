package com.watch.watch_mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.watch.watch_mall.model.dto.user.UserAdminQueryRequest;
import com.watch.watch_mall.model.entity.User;
import com.watch.watch_mall.model.vo.LoginUserVO;
import com.watch.watch_mall.model.vo.UserAdminDetailVO;
import com.watch.watch_mall.model.vo.UserAdminPageVO;
import com.watch.watch_mall.model.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
* @author Lu
* @description 针对表【user】的数据库操作Service
* @createDate 2025-12-26 11:41:05
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount, String userPassword, String checkPassword);

    LoginUserVO userLogin(String userAccount, String userPassword, Boolean rememberMe, HttpServletRequest request, HttpServletResponse response);

    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    LoginUserVO getHandledUser(User user);

    boolean userLogout(HttpServletRequest request);

    LoginUserVO getLoginUserVO(User user);

    UserVO getUserVO(User user);

    Page<UserAdminPageVO> pageAdminUsers(UserAdminQueryRequest queryRequest);

    UserAdminDetailVO getAdminUserDetail(Long userId);

    boolean updatePassword(User loginUser, String oldPassword, String newPassword, String checkPassword, HttpServletRequest request);
}
