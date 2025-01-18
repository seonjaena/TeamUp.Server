package com.sjna.teamup.common.service.port;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface SmsSender {

    SingleMessageSentResponse sendOneMessage(String to, String messageText);

}
