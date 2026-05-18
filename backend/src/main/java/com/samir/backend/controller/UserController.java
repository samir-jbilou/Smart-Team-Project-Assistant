package com.samir.backend.controller;

import com.samir.backend.entity.User;
import com.samir.backend.entity.enums.UserRole;
import com.samir.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/members")
    public List<User> getOnlyMembers() {
        return userService.getMembers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/characteristics")
    public User updateCharacteristics(@PathVariable Long id, @RequestBody User updateData) {
        return userService.updateUserCharacteristics(id, updateData.getSeniorityLevel(), updateData.getEfficiencyScore(), updateData.getPrimarySkill());
    }
}