package com.sjna.teamup.common.security;

import com.sjna.teamup.common.domain.exception.DecryptionException;
import com.sjna.teamup.common.domain.exception.EncryptionException;
import com.sjna.teamup.common.service.port.LocaleHolder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.sjna.teamup.common.service.port.MessageSourceHolder;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Locale;

// TODO: EncryptionProvider를 Interface를 구현하는 클래스로 변경
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionProvider {

    private KeyPair keyPair;
    private final MessageSourceHolder messageSourceHolder;
    private final LocaleHolder localeHolder;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }catch(NoSuchAlgorithmException e) {
            log.error(messageSourceHolder.getMessage("error.keypair.fail", null, Locale.KOREA), e);
            throw new RuntimeException(messageSourceHolder.getMessage("error.keypair.fail", null, localeHolder.getLocale()), e);
        }
    }

    public String encrypt(String plainText) throws EncryptionException {
        Locale locale = localeHolder.getLocale();
        if(StringUtils.isEmpty(plainText)) {
            throw new EncryptionException(messageSourceHolder.getMessage("error.encrypt.fail", null, locale));
        }
        Cipher cipher;
        byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);

        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainTextBytes));
        }catch(Exception e) {
            log.error(messageSourceHolder.getMessage("error.encrypt.fail", null, locale), e);
            throw new EncryptionException(messageSourceHolder.getMessage("error.encrypt.fail", null, locale), e);
        }

    }

    public String decrypt(String encryptedText) throws EncryptionException {
        Locale locale = localeHolder.getLocale();

        if (StringUtils.isEmpty(encryptedText)) {
            throw new DecryptionException(messageSourceHolder.getMessage("error.decrypt.fail", null, locale));
        }

        try {
            byte[] encryptedTextBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return new String(cipher.doFinal(encryptedTextBytes), StandardCharsets.UTF_8);
        } catch(Exception e) {
            log.error(messageSourceHolder.getMessage("error.decrypt.fail", null, locale), e);
            throw new DecryptionException(messageSourceHolder.getMessage("error.decrypt.fail", null, locale), e);
        }
    }
}
