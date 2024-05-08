package com.sjna.teamup.security;

import com.sjna.teamup.exception.DecryptionException;
import com.sjna.teamup.exception.EncryptionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionProvider {

    private KeyPair keyPair;
    private final MessageSource messageSource;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }catch(NoSuchAlgorithmException e) {
            log.error(messageSource.getMessage("error.keypair.fail", null, Locale.KOREA), e);
            throw new RuntimeException(messageSource.getMessage("error.keypair.fail", null, Locale.KOREA), e);
        }
    }

    public String encrypt(String plainText) throws EncryptionException {
        Locale locale = LocaleContextHolder.getLocale();
        if(StringUtils.isEmpty(plainText)) {
            throw new EncryptionException(messageSource.getMessage("error.encrypt.fail", null, locale));
        }
        Cipher cipher;
        byte[] plainTextBytes = plainText.getBytes(StandardCharsets.UTF_8);

        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainTextBytes));
        }catch(Exception e) {
            log.error(messageSource.getMessage("error.encrypt.fail", null, locale), e);
            throw new EncryptionException(messageSource.getMessage("error.encrypt.fail", null, locale), e);
        }

    }

    public String decrypt(String encryptedText) throws EncryptionException {
        Locale locale = LocaleContextHolder.getLocale();

        if (StringUtils.isEmpty(encryptedText)) {
            throw new DecryptionException(messageSource.getMessage("error.decrypt.fail", null, locale));
        }

        try {
            byte[] encryptedTextBytes = Base64.getDecoder().decode(encryptedText);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return new String(cipher.doFinal(encryptedTextBytes), StandardCharsets.UTF_8);
        } catch(Exception e) {
            log.error(messageSource.getMessage("error.decrypt.fail", null, locale), e);
            throw new DecryptionException(messageSource.getMessage("error.decrypt.fail", null, locale), e);
        }
    }
}
