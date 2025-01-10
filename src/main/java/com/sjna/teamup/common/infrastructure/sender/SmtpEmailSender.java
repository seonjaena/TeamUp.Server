package com.sjna.teamup.common.infrastructure.sender;

import com.sjna.teamup.common.domain.exception.SendEmailFailureException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender emailSender;
    private final MessageSource messageSource;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void sendRawEmail(List<String> to, String subject, String content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(from);
            helper.setTo(to.toArray(new String[to.size()]));
            helper.setSubject(subject);
            helper.setText(content, true);

            emailSender.send(mimeMessage);
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new SendEmailFailureException(
                    messageSource.getMessage("error.send-email.fail",
                            new String[] {to.toString()},
                            LocaleContextHolder.getLocale()
                    )
            );
        }
    }

}
