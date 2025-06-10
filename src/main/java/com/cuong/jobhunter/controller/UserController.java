package com.cuong.jobhunter.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cuong.jobhunter.domain.User;
import com.cuong.jobhunter.dto.ResCreateUserDTO;
import com.cuong.jobhunter.dto.ResultPaginationDTO;
import com.cuong.jobhunter.service.UserService;
import com.cuong.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user) throws IdInvalidException {
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        if (this.userService.isEmailExsist(user.getEmail())) {
            throw new IdInvalidException("Email" + user.getEmail() + "đã tồn tại , hãy thay bằng email khác");
        }
        User newUser = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getListUser(@Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.userService.getAllUser(spec, pageable));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserWithId(@PathVariable Long id) throws IdInvalidException {
        User user = this.userService.getUserWithId(id);
        if (user == null) {
            throw new IdInvalidException("id" + user.getId() + "không tồn tại ");
        }
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUser(@RequestBody User user, @PathVariable Long id)
            throws IdInvalidException {
        if (this.userService.isIdValid(id) == false) {
            throw new IdInvalidException("Id" + user.getId() + "không tồn tại trong hệ thống ");
        }
        this.userService.updateUSer(user, id);
        return ResponseEntity.ok().body("Update successfully");
    }

    @DeleteMapping("/user{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws IdInvalidException {
        if (this.userService.isIdValid(id) == false) {
            throw new IdInvalidException("Không tồn tại ID để xóa");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.ok().body("Delete successfully !!!");
    }

}
