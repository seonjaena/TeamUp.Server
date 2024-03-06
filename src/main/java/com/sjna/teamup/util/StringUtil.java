package com.sjna.teamup.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class StringUtil {

    public static String getMd5(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String getVerification6DigitCode() {
        Random rnd = new Random(System.currentTimeMillis());
        return String.valueOf( ((1 + rnd.nextInt(2)) * 10000 + rnd.nextInt(10000)) );
    }

}
