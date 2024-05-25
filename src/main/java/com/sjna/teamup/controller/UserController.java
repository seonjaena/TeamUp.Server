package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.ChangePasswordRequest;
import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.response.ProfileImageUrlResponse;
import com.sjna.teamup.dto.response.TestResponse;
import com.sjna.teamup.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public String test() {
        return "hello";
    }

    @GetMapping(value = "/available/userId/{userId}")
    public boolean checkUserIdAvailable(
            @Pattern(regexp = "^[a-zA-Z90-9\\-_]{5,20}$", message = "constraint.user-id.pattern")
            @PathVariable(name = "userId") String userId) {
        return userService.checkUserIdAvailable(userId);
    }

    @GetMapping(value = "/available/userNickname/{userNickname}")
    public boolean checkUserNicknameAvailable(
            @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "constraint.user-nickname.pattern")
            @PathVariable(name = "userNickname") String userNickname) {
        return userService.checkUserNicknameAvailable(userNickname);
    }

    @PostMapping
    public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.signUp(signUpRequest);
    }

    @GetMapping(value = "/link/password/{userId}")
    public void sendChangePasswordUrl(
            @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "constraint.user-id.pattern")
            @PathVariable(name = "userId") String userId) {
        userService.sendChangePasswordUrl(userId);
    }

    @PatchMapping(value = "/password")
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
    }

    @PatchMapping(value = "/nickname/{userNickname}")
    private void changeNickname(
            @Pattern(regexp = "^(?!_)(?=.*[a-zA-Z0-9가-힣_])[a-zA-Z0-9가-힣_]{2,16}(?<!_)$", message = "constraint.user-nickname.pattern")
            @PathVariable(name = "userNickname") String userNickname,
            Principal principal) {
        userService.changeNickname(principal.getName(), userNickname);
    }

    @PatchMapping(value = "/birth/{userBirth}")
    public void changeBirth(@PathVariable(name = "userBirth") LocalDate userBirth, Principal principal) {
        userService.changeBirth(principal.getName(), userBirth);
    }

    @PatchMapping(value = "/profile-image")
    public void changeProfileImage(@RequestParam("profileImage") MultipartFile profileImage, Principal principal) {
        userService.changeProfileImage(principal.getName(), profileImage);
    }

    @GetMapping(value = "/profile-image-url")
    public ProfileImageUrlResponse getProfileImageUrl(Principal principal) {
        return userService.getProfileImageUrl(principal.getName());
    }

    // 테스트 용도의 임시 URL
    @GetMapping(value = "/test")
    public TestResponse a(Principal principal) {
        return userService.a(principal.getName());
    }

}
