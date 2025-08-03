package com.cuong.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cuong.jobhunter.domain.User;
import com.cuong.jobhunter.dto.ResultPagination;
import com.cuong.jobhunter.service.UserService;
import com.turkraft.springfilter.boot.Filter;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/user")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPagination> getListUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.userService.getAllUser(spec, pageable));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserWithId(@PathVariable Long id) {
        User user = this.userService.getUserWithId(id);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUser(@RequestBody User user, @PathVariable Long id) {
        this.userService.updateUSer(user, id);
        return ResponseEntity.ok().body("Update successfully");
    }

    @DeleteMapping("/user{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body("Delete successfully !!!");
    }

}
