package com.sjna.teamup.controller;

import com.sjna.teamup.dto.request.ChangePasswordRequest;
import com.sjna.teamup.dto.request.LoginChangePasswordRequest;
import com.sjna.teamup.dto.request.SignUpRequest;
import com.sjna.teamup.dto.response.ProfileImageUrlResponse;
import com.sjna.teamup.dto.response.UserProfileInfoResponse;
import com.sjna.teamup.service.UserService;
import com.sjna.teamup.validator.constraint.UserIdConstraint;
import com.sjna.teamup.validator.constraint.UserNicknameConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Validated
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
    public void sendChangePasswordUrl(@PathVariable(name = "userId") @UserIdConstraint String userId) {
        userService.sendChangePasswordUrl(userId);
    }

    @PatchMapping(value = "/password")
    public void findPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.findPassword(changePasswordRequest);
    }

    @PatchMapping(value = "/authenticated/password")
    public void changePassword(@Valid @RequestBody LoginChangePasswordRequest changePasswordRequest,
                               Principal principal) {
        userService.changePassword(principal.getName(), changePasswordRequest);
    }

    @PatchMapping(value = "/nickname/{userNickname}")
    public void changeNickname(@PathVariable(name = "userNickname") @UserNicknameConstraint String userNickname,
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

    @GetMapping(value = "/profile-info")
    public UserProfileInfoResponse getProfileInfo(Principal principal) {
        return userService.getProfileInfo(principal.getName());
    }

    @DeleteMapping
    public void delete(Principal principal) {
        userService.delete(principal.getName());
    }

    @DeleteMapping(value = "/temp")
    public void deleteTmp(Principal principal) {
        userService.deleteTmp(principal.getName());
    }

}
