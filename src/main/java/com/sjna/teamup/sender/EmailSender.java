package com.sjna.teamup.sender;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.sjna.teamup.exception.SendEmailFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final AmazonSimpleEmailService amazonSimpleEmailService;

    @Value("${aws.ses.send-email}")
    private String sendEmail;

    public void sendRawEmail(List<String> to, String subject, String content) {
        try {
            Destination destination = new Destination()
                    .withToAddresses(to);

            Message message = new Message()
                    .withSubject(createContent(subject))
                    .withBody(new Body()
                            .withHtml(createContent(content)));

            SendEmailRequest sendEmailRequest = new SendEmailRequest()
                    .withSource(sendEmail)
                    .withDestination(destination)
                    .withMessage(message);

            SendEmailResult sendEmailResult = amazonSimpleEmailService
                    .sendEmail(sendEmailRequest);

            if (sendEmailResult.getSdkHttpMetadata().getHttpStatusCode() != HttpStatus.OK.value()) {
                throw new SendEmailFailureException("failed to send email. target email=" + to.toString());
            }
        }catch(AmazonSimpleEmailServiceException e) {
            log.error(e.getMessage());
            throw new SendEmailFailureException("Failed to send email. target email=" + to.toString());
        }
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }

}
