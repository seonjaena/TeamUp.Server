package com.sjna.teamup.resume.service.port;

import com.sjna.teamup.resume.domain.Resume;
import com.sjna.teamup.user.domain.User;

public interface ResumeRepository {

    Resume saveAndFlush(Resume resume);
    Resume getByUser(User user);

}
