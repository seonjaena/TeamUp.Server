package com.sjna.teamup.common.infrastructure.sender;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SMSSender {

    @Value("${cloud.cool-sms.send-phone}")
    private String sendEmail;

    private final DefaultMessageService messageService;
    private final MessageSource messageSource;

    public SMSSender(MessageSource messageSource, @Value("${cloud.cool-sms.credentials.access-key}") String accessKey, @Value("${cloud.cool-sms.credentials.secret-key}") String secretKey) {
        this.messageSource = messageSource;
        this.messageService = NurigoApp.INSTANCE.initialize(accessKey, secretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendOneMessage(String to, String messageText) {
        Message message = new Message();
        message.setFrom(sendEmail);
        message.setTo(to);
        message.setText(messageText);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return response;
    }

}
