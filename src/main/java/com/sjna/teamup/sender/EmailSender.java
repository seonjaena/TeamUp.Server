package com.sjna.teamup.sender;

import java.util.List;

public interface EmailSender {

    void sendRawEmail(List<String> to, String subject, String content);


}
