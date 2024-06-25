package com.sjna.teamup.sender;

import com.sjna.teamup.exception.SendEmailFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
//@Primary
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender emailSender;
    private final MessageSource messageSource;

    @Override
    public void sendRawEmail(List<String> to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@sjna.xyz"); // 보낸 사람 설정
            message.setTo(to.toArray(new String[to.size()]));
            message.setSubject(subject);
            message.setText(content);
            emailSender.send(message);
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
