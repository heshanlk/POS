/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heidi.services;

/**
 *
 * @author mujahid
 */
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

import com.heidi.hardware.utils.LocalMachine;

public class KeyAuthorization {

    private final String heidiKey = "sri-@)!)company@he1d@SOFT.com-sl-nw";

    LocalMachine localMachine;

    public KeyAuthorization(){
        localMachine = new LocalMachine();
    }


    public boolean getKeyStatus(String ownerName, String ownerEmail, String ownerMobile, String key) throws Exception {
        Map<String, String> params = new HashMap<String, String>();

        params.put("owner", "\"" + ownerName + "\"");
        params.put("email", "\"" + ownerEmail + "\"");
        params.put("mobile", "\"" + ownerMobile + "\"");
        params.put("key", "\"" + key + "\"");

        Map<String, String> tmpParams = new HashMap<String, String>();
       
        DrupalJsonServiceClient serviceClient = new DrupalJsonServiceClient();
        JSONObject serviceJsonResult = serviceClient.callMethod("hpos.key_setup", params);

        boolean messageStatus =  serviceJsonResult.getBoolean("status_message");
        int result = serviceJsonResult.getInt("result");
        int keyStatus = serviceJsonResult.getInt("key_status");

        if (messageStatus) {
            if (result == 5001 && keyStatus == 1001) {
                return true;
            }
        }
        return false;
    }

    public String getHeidiKey() {
        return this.heidiKey;
    }

    public boolean checkLocalKey(String encryptedLocalKey, String encryptedLicenseKey) throws Exception {
        String generatedKey = this.getMD5(encryptedLicenseKey + this.windowsLocalKey()  + this.heidiKey);
        if(encryptedLocalKey != null){
            if(encryptedLocalKey.equals(generatedKey)){
                return true;
            }
        }
        return false;
    }

   

    public String getMD5(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String pass = text + this.heidiKey;
        byte[] defaultBytes = pass.getBytes();

        MessageDigest algorithm = MessageDigest.getInstance("MD5");
        algorithm.reset();
        algorithm.update(defaultBytes);
        byte messageDigest[] = algorithm.digest();

        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xFF & messageDigest[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public String writableWindowsLocalKey(String key) throws Exception{
        String encryptedLicenseKey = this.getEncryptedLicenseKey(key);
        String encryptedWindowsKey = this.windowsLocalKey();
        return this.getMD5(encryptedLicenseKey + encryptedWindowsKey + this.heidiKey );
    }

    

    public String windowsLocalKey() throws Exception{
        String hardDriveSerial = this.localMachine.getDiskSerialNumber("C");
        //String hardDriveSerial = "12345";
        javax.swing.JOptionPane.showConfirmDialog((java.awt.Component) null,hardDriveSerial, "Hard drive Activation",
                        javax.swing.JOptionPane.DEFAULT_OPTION);
        return this.getMD5(hardDriveSerial + this.heidiKey);
    }

    public String getEncryptedLicenseKey(String key) throws Exception{
            return this.getMD5(key + this.heidiKey);
    }
}
