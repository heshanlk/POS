/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heidi.hardware.utils;

/**
 *
 * @author mujahid
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class LocalMachine {

    public String getMacAddress() throws SocketException, UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();

        System.out.println(address);
        NetworkInterface ni = NetworkInterface.getByInetAddress(address);

        if (ni != null) {
            byte[] mac = ni.getHardwareAddress();
            if (mac != null) {
                for (int i = 0; i < mac.length; i++) {
                    System.out.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
                }
                System.out.println();
                System.out.println(mac);
            } else {
                System.out.println("Address doesn't exist or is not accessible.");
            }
        } else {
            System.out.println("Network Interface for the specified address is not found.");
        }

        return "";
    }

    public static String getMotherboardSerial() throws Exception {
        String result = "";
        String timeStamp =  Long.toString(System.currentTimeMillis());
        File file = File.createTempFile("mbs" + timeStamp, ".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs =
                "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                + "Set colItems = objWMIService.ExecQuery _ \n"
                + "   (\"Select * from Win32_BaseBoard\") \n"
                + "For Each objItem in colItems \n"
                + "    Wscript.Echo objItem.SerialNumber \n"
                + "    exit for  ' do the first cpu only! \n"
                + "Next \n";

        fw.write(vbs);
        fw.close();
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            result += line;
        }
        input.close();
        file.delete();
        return result.trim();
    }

    public static String getDiskSerialNumber(String drive) throws Exception {
        String result = "";
        String timeStamp =  Long.toString(System.currentTimeMillis());
        File file = File.createTempFile("hd"+timeStamp, ".vbs");
        file.deleteOnExit();
        FileWriter fw = new java.io.FileWriter(file);

        String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                + "Set colDrives = objFSO.Drives\n"
                + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
                + "Wscript.Echo objDrive.SerialNumber";  // see note
        fw.write(vbs);
        fw.close();
        Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
        BufferedReader input =
                new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            result += line;
        }
        input.close();
        file.delete();
        return result.trim();
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }
}
