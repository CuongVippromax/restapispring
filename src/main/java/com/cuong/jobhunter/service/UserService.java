package com.cuong.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.cuong.jobhunter.domain.User;
import com.cuong.jobhunter.dto.Meta;
import com.cuong.jobhunter.dto.ResCreateUserDTO;
import com.cuong.jobhunter.dto.ResultPaginationDTO;
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
        newUser.setAddress(user.getAddress());
        newUser.setAge(user.getAge());
        newUser.setGender(user.getGender());
        this.userRepository.save(newUser);
        return newUser;
    }

    public Boolean isEmailExsist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO userDTO = new ResCreateUserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setAddress(user.getAddress());
        userDTO.setCreatedAt(user.getCreatedAt());
        return userDTO;
    }

    public ResultPaginationDTO getAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        // pageable là là lấy từ phía frontend còn pageUser là lấy giá trị querry từ db
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(pageUser.getContent());
        return resultPaginationDTO;
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

    public Boolean isIdValid(long id) {
        return this.userRepository.existsById(id);
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

    public void updateUSerToken(String token, String email) {
        User currentUser = this.getUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
