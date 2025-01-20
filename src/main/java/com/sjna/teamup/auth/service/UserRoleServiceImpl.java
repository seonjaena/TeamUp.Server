package com.sjna.teamup.auth.service;

import com.sjna.teamup.auth.controller.port.UserRoleService;
import com.sjna.teamup.auth.domain.UserRole;
import com.sjna.teamup.auth.service.port.UserRoleRepository;
import com.sjna.teamup.common.domain.exception.UserRoleNotExistException;
import com.sjna.teamup.common.service.port.LocaleHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final MessageSource messageSource;
    private final UserRoleRepository userRoleRepository;
    private final LocaleHolder localeHolder;

    public UserRole getBasic() {
        return userRoleRepository.findAll(Sort.by(Sort.Direction.DESC, "priority")).stream().findFirst()
                .orElseThrow(() -> new UserRoleNotExistException(
                        messageSource.getMessage("error.common.500", null, localeHolder.getLocale())
                ));
    }

    public String getRoleHierarchy() {
        // local 프로필인 경우 In Memory DB를 사용하기 때문에 USER_ROLE에 기본 데이터를 넣어준다.
        initLocalDbData();

        List<UserRole> userRoles = userRoleRepository.findAll(Sort.by(Sort.Direction.ASC, "priority"));

        StringBuilder roleStr = new StringBuilder();

        roleStr.append(userRoles.get(0).getName());

        for(int i = 1; i < userRoles.size(); i++) {
            roleStr.append(" > ");
            roleStr.append(userRoles.get(i).getName());
        }

        return roleStr.toString();
    }

    // TODO: 테스트를 해야 할 것인지, 한다면 어떻게 하면 테스트 용이성이 좋아질지 고민 필요
    private void initLocalDbData() {
        if(activeProfile.equals("local")) {
            List<String> roleNames = Arrays.asList("ADMIN", "PRIVATE_GOLD", "PRIVATE_SILVER", "PRIVATE_BRONZE");

            for(int i = 1; i <= roleNames.size(); i++) {
                userRoleRepository.save(UserRole.builder()
                        .name(roleNames.get(i - 1))
                        .priority(i)
                        .build());
            }
        }
    }

}
