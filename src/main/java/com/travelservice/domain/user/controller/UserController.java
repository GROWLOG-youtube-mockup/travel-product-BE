package com.travelservice.domain.user.controller;

import com.travelservice.domain.user.dto.UserRegistrationRequestDto;
import com.travelservice.domain.user.entity.User;
import com.travelservice.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> registerMember(@RequestBody UserRegistrationRequestDto requestDto) {
        User user = userService.registerMember(requestDto);
        return ResponseEntity.ok(user);
    }
}
