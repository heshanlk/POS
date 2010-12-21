/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heidi.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;


import org.json.JSONObject;

/**
 *
 * @author mujahid
 */
public class DrupalJsonServiceClient {

    private String domainName = "http://localhost/";
    private String methodName = "stripe.reseller.get_by_id";
    private String serviceURL = "http://localhost/drupal-6/services/json";
    private String serviceDomain = "http://localhost/";
    private String apiKey = "ebc43bfdee2b00695db7d31c829f0af8";
    
    DrupalServiceUtils serviceUtil;

    public DrupalJsonServiceClient() {
        serviceUtil = new DrupalServiceUtils();
    }

    public JSONObject callMethod(String methodName, Map<String, String> methodParams) throws Exception {
        this.setMethodName(methodName);
        NameValuePair[] postParams = this.getPostParams(methodParams);

        HttpClient client = new HttpClient();
        client.getParams().setParameter("http.useragent", "POS Client");
        BufferedReader br = null;
        PostMethod method = new PostMethod(this.serviceURL);

        method.setRequestBody(postParams);
        int returnCode = client.executeMethod(method);
        JSONObject jsonResponse = new JSONObject();

        if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
            method.getResponseBodyAsString();
        } else {
            br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
            String readLine;
            StringBuilder sb = new StringBuilder();
            while (((readLine = br.readLine()) != null)) {
                sb.append(readLine);
                //System.out.println(readLine);
            }
            jsonResponse = new JSONObject(sb.toString());
            jsonResponse = new JSONObject(jsonResponse.getString("#data"));
        }
        return jsonResponse;

    }

    public NameValuePair[] getPostParams(Map<String, String> methodParams) throws Exception {

        String nonce = serviceUtil.generateNonce(10);
        String timeStamp = serviceUtil.getCurrentTimeStamp();
        String hashMessage = timeStamp + ";" + this.serviceDomain + ";" + nonce;
        hashMessage += ";" + this.methodName;

        // Default Params
        NameValuePair hashPair = new NameValuePair("hash", "\"" + serviceUtil.generateHmacHash(hashMessage, this.apiKey) + "\"");
        NameValuePair domainNamePair = new NameValuePair("domain_name", "\"" + this.domainName + "\"");
        NameValuePair timeStampPair = new NameValuePair("domain_time_stamp", "\"" + timeStamp + "\"");
        NameValuePair noncePair = new NameValuePair("nonce", "\"" + nonce + "\"");
        NameValuePair methodNamePair = new NameValuePair("method", "\"" + this.methodName + "\"");
        //

        NameValuePair[] postFields = new NameValuePair[5 + methodParams.size()];

        postFields[0] = hashPair;
        postFields[1] = domainNamePair;
        postFields[2] = timeStampPair;
        postFields[3] = noncePair;
        postFields[4] = methodNamePair;

        //NameValuePair resellerIDPair = new NameValuePair("reseller_id", "1");
        Iterator it = methodParams.entrySet().iterator();
        int i = 5;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            postFields[i] = new NameValuePair(pairs.getKey().toString(), pairs.getValue().toString());
            i++;
        }

        return postFields;
    }


    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getServiceDomain() {
        return serviceDomain;
    }

    public void setServiceDomain(String serviceDomain) {
        this.serviceDomain = serviceDomain;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    private void generateHash() {
    }
}
