package com.cuong.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cuong.jobhunter.domain.User;
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

    public List<User> getAllUser() {
        return this.userRepository.findAll();
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
}
