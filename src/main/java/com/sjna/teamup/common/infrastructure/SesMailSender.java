package com.sjna.teamup.common.infrastructure;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.sjna.teamup.common.domain.exception.SendEmailFailureException;
import com.sjna.teamup.common.service.port.LocaleHolder;
import com.sjna.teamup.common.service.port.MailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SesMailSender implements MailSender {

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final MessageSource messageSource;
    private final LocaleHolder localeHolder;

    @Value("${cloud.aws.ses.send-email}")
    private String sendEmail;

    @Override
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
                throw new AmazonSimpleEmailServiceException("Failed to send email. HttpStatus=" + sendEmailResult.getSdkHttpMetadata().getHttpStatusCode());
            }
        }catch(AmazonSimpleEmailServiceException e) {
            log.error(e.getMessage());
            throw new SendEmailFailureException(
                    messageSource.getMessage("error.send-email.fail",
                            new String[] {to.toString()},
                            localeHolder.getLocale()
                    )
            );
        }
    }

    private Content createContent(String text) {
        return new Content()
                .withCharset("UTF-8")
                .withData(text);
    }

}
