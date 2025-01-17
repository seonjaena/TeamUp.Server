package com.sjna.teamup.user.controller.port;

import com.sjna.teamup.common.domain.FILTER_INCLUSION_MODE;
import com.sjna.teamup.user.controller.request.ChangePasswordRequest;
import com.sjna.teamup.user.controller.request.LoginChangePasswordRequest;
import com.sjna.teamup.user.controller.request.SignUpRequest;
import com.sjna.teamup.user.controller.response.ProfileImageUrlResponse;
import com.sjna.teamup.user.controller.response.UserProfileInfoResponse;
import com.sjna.teamup.user.domain.USER_STATUS;
import com.sjna.teamup.user.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public interface UserService extends UserDetailsService {

    User getUser(String userId);
    User getUser(String userId, USER_STATUS[] userStatuses, FILTER_INCLUSION_MODE filterInclusionMode);
    User getNotDeletedUser(String userId);
    boolean checkUserIdAvailable(String userId);
    boolean checkUserPhoneAvailable(String phone);
    boolean checkUserNicknameAvailable(String userNickname);
    void signUp(SignUpRequest signUpRequest);
    void sendChangePasswordUrl(String userId);
    void findPassword(ChangePasswordRequest changePasswordRequest);
    void changePassword(String userId, LoginChangePasswordRequest changePasswordRequest);
    void changeNickname(String userId, String userNickname);
    void changeBirth(String userId, LocalDate userBirth);
    void changeProfileImage(String userId, MultipartFile profileImage);
    void changeUserPhone(String userId, String userPhone);
    ProfileImageUrlResponse getProfileImageUrl(String userId);
    UserProfileInfoResponse getProfileInfo(String userId);
    void delete(String userId);
    void deleteTmp(String userId);

}
