package com.sjna.teamup.resume.infrastructure;

import com.sjna.teamup.common.domain.exception.ResumeNotFoundException;
import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.resume.service.port.ResumeRepository;
import com.sjna.teamup.user.domain.User;
import com.sjna.teamup.user.infrastructure.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepository {

    private final ResumeJpaRepository resumeJpaRepository;

    @Override
    public Resume saveAndFlush(Resume resume) {
        return resumeJpaRepository.saveAndFlush(ResumeEntity.fromDomain(resume)).toDomain();
    }

    @Override
    public Resume getByUser(User user) {
        return resumeJpaRepository.findByUser(UserEntity.fromDomain(user))
                .orElseThrow(() -> new ResumeNotFoundException( "Resume Not Found. userId=" + user.getAccountId())).toDomain();
    }

}
