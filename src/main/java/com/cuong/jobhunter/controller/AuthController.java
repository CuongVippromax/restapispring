package com.cuong.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cuong.jobhunter.domain.User;
import com.cuong.jobhunter.dto.LoginDTO;
import com.cuong.jobhunter.dto.ResLoginDTO;
import com.cuong.jobhunter.dto.ResLoginDTO.UserLogin;
import com.cuong.jobhunter.service.UserService;
import com.cuong.jobhunter.util.SecurityUtil;
import com.cuong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO logindto) {
                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                logindto.getUsername(), logindto.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // nạp thông tin (nếu xử lý thành công) vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();
                User userDb = this.userService.getUserByUsername(logindto.getUsername());
                if (userDb != null) {
                        UserLogin resUserLogin = new ResLoginDTO.UserLogin(
                                        userDb.getId(),
                                        userDb.getEmail(),
                                        userDb.getName());
                        res.setUser(resUserLogin);
                }
                // Tao access Token
                String accessToken = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());

                res.setAccessToken(accessToken);

                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(logindto.getUsername(), res);

                // update user
                this.userService.updateUserToken(refresh_token, logindto.getUsername());

                // set cookies
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/refresh")
        public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
                        throws IdInvalidException {
                // check valid
                Jwt decodedRefreshToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedRefreshToken.getSubject();
                // check user by token + email
                User currentUser = this.userService.getUSerByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser != null) {
                        throw new IdInvalidException("Refresh Token khong hop le");
                }
                ResLoginDTO res = new ResLoginDTO();
                User userDb = this.userService.getUserByUsername(email);
                if (userDb != null) {
                        UserLogin resUserLogin = new ResLoginDTO.UserLogin(
                                        userDb.getId(),
                                        userDb.getEmail(),
                                        userDb.getName());
                        res.setUser(resUserLogin);
                }
                // Tao access Token
                String accessToken = this.securityUtil.createAccessToken(email, res.getUser());

                res.setAccessToken(accessToken);

                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

                // update user
                this.userService.updateUserToken(new_refresh_token, email);

                // set cookies
                ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @PostMapping("/auth/logout")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("Email doesn't exsits");
                }
                // update refresh token = null
                this.userService.updateUserToken(null, email);
                // remove refresh token cookies
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }
}
