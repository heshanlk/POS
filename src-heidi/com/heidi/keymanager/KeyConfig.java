/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.heidi.keymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author mujahid
 */
public class KeyConfig {


    private static Logger logger = Logger.getLogger("com.heidi.keymanager.KeyConfig");
    private Properties m_propsconfig;
    private File configfile;

   
    public KeyConfig() {
        this.configfile = getDefaultConfig();
        init(configfile);
    }

    private void init(File configfile) {
        this.configfile = configfile;
        m_propsconfig = new Properties();

        logger.info("Reading configuration file: " + configfile.getAbsolutePath());
    }

    private File getDefaultConfig() {
        return new File(new File(System.getProperty("user.home")), "heidipos_manager.properties");
    }

    public String getProperty(String sKey) {
        return m_propsconfig.getProperty(sKey);
    }

    public String getHost() {
        return getProperty("machine.hostname");
    }

    public File getConfigFile() {
        return configfile;
    }

    public void setProperty(String sKey, String sValue) {
        if (sValue == null) {
            m_propsconfig.remove(sKey);
        } else {
            m_propsconfig.setProperty(sKey, sValue);
        }
    }

    private String getLocalHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException eUH) {
            return "localhost";
        }
    }

    public boolean delete() {
        loadDefault();
        return configfile.delete();
    }

    public void load() {
        try {
            InputStream in = new FileInputStream(configfile);
            if (in != null) {
                m_propsconfig.load(in);
                in.close();
            }
        } catch (IOException e) {
           
        }

    }

    public void save() throws IOException {

        OutputStream out = new FileOutputStream(configfile);
        if (out != null) {
            m_propsconfig.store(out,  "HEIDI POS . Configuration file.");
            out.close();
        }
    }

    private void loadDefault() {

        m_propsconfig = new Properties();

        String dirname = System.getProperty("dirname.path");
        dirname = dirname == null ? "./" : dirname;



        m_propsconfig.setProperty("machine.local", "point");
        m_propsconfig.setProperty("machine.install", "h_POS");
    }

}
