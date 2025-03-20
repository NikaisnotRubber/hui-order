package com.hui.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hui.dto.UserDTO;
import com.hui.entity.User;

public interface UserService extends IService<User> {
    
    /**
     * 通過電子郵件查找用戶
     */
    User getUserByEmail(String email);
    
    /**
     * 通過ID獲取用戶DTO
     */
    UserDTO getUserDTOById(Long id);
    
    /**
     * 註冊新用戶
     */
    User registerUser(User user);
    
    /**
     * 更新用戶信息
     */
    void updateUser(User user);

    String getUserRoleById(Long id);
}
