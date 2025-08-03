package com.cuong.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.cuong.jobhunter.domain.User;
import com.cuong.jobhunter.dto.Meta;
import com.cuong.jobhunter.dto.ResultPagination;
import com.cuong.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setName(user.getName());
        newUser.setPassword(user.getPassword());
        this.userRepository.save(newUser);
        return newUser;
    }

    public ResultPagination getAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPagination result = new ResultPagination();
        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageUser.getContent());
        return result;
    }

    public User getUserWithId(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            User usergetted = user.get();
            return usergetted;
        } else {
            return null;
        }
    }

    public void updateUSer(User user, Long id) {
        Optional<User> getUser = this.userRepository.findById(id);
        if (getUser.isPresent()) {
            User userupdate = getUser.get();
            userupdate.setEmail(user.getEmail());
            userupdate.setName(user.getName());
            userupdate.setPassword(user.getPassword());
            this.userRepository.save(userupdate);
        }
    }

    public void deleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUSerByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
