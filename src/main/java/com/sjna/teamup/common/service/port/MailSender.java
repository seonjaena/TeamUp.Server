package com.sjna.teamup.common.service.port;

import java.util.List;

public interface MailSender {

    void sendRawEmail(List<String> to, String subject, String content);

}
