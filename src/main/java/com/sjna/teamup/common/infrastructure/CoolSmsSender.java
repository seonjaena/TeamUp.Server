package com.sjna.teamup.common.infrastructure;

import com.sjna.teamup.common.service.port.SmsSender;
import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoolSmsSender implements SmsSender {

    @Value("${cloud.cool-sms.send-phone}")
    private String sendEmail;
    @Value("${cloud.cool-sms.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.cool-sms.credentials.secret-key}")
    private String secretKey;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(accessKey, secretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendOneMessage(String to, String messageText) {
        Message message = new Message();
        message.setFrom(sendEmail);
        message.setTo(to);
        message.setText(messageText);

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

}
