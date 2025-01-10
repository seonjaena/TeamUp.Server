package com.sjna.teamup.common.infrastructure.sender;

import java.util.List;

public interface EmailSender {

    void sendRawEmail(List<String> to, String subject, String content);


}
