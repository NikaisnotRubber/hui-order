package com.hui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hui.dto.UserDTO;
import com.hui.entity.User;
import com.hui.mapper.UserMapper;
import com.hui.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    public UserDTO getUserDTOById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    
    @Override
    public User registerUser(User user) {
        // 加密密碼
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 確保角色設置
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // 若未設置，預設為普通用戶
        }
        
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public void updateUser(User user) {
        // 如果密碼字段不為空，則加密密碼
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.updateById(user);
    }

    @Override
    public String getUserRoleById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return user.getRole();
    }
}