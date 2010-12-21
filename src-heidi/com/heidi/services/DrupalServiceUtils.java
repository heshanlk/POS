/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heidi.services;

import java.nio.charset.Charset;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author mujahid
 */
public class DrupalServiceUtils {

    final Charset asciiCs = Charset.forName("US-ASCII");
    private Mac apikeyMac;
    
    public  String generateNonce(int length) {
        char[] allChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
        Random generator = new Random();
        String nonce = "";
        for (int i = 0; i < length; i++) {
            nonce += allChars[generator.nextInt(allChars.length)];
        }
        return nonce;
    }

    public String generateHmacHash(String message,String apiKey) throws Exception {
        byte[] hash = getApikeyMac(apiKey).doFinal(asciiCs.encode(message).array());
        String result = "";
        for (int i = 0; i < hash.length; i++) {
            result += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    private Mac getApikeyMac(String apiKey) throws Exception {
        if (apikeyMac == null) {
            SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(asciiCs.encode(apiKey).array(), "HmacSHA256");
            apikeyMac = javax.crypto.Mac.getInstance("HmacSHA256");
            apikeyMac.init(keySpec);
        }
        return apikeyMac;
    }

    public String getCurrentTimeStamp(){
        return Long.toString(System.currentTimeMillis());
    }




}
