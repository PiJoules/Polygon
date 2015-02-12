/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Pi_Joules
 */
public class HTTPManager {
    
    private final Activity a;
    private final String url = "http://polygon-server.appspot.com"; // just using space on my current website
    private final String ERR = "err";
    private String httpResponse = "";
    
    public HTTPManager(Activity a){
        this.a = a;
    }
    
    public boolean post(String scoreString, String delimeter, String delimeter2){
        if (!isOnline(a)){
            httpResponse = "not connected to internet";
            return false;
        }
        try{
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpPost httppost = new HttpPost(url);
            
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("scoreString", scoreString));
            nameValuePairs.add(new BasicNameValuePair("delimeter", delimeter));
            nameValuePairs.add(new BasicNameValuePair("delimeter2", delimeter2));
            nameValuePairs.add(new BasicNameValuePair("errorflag", ERR));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            // set timeout parameters
            HttpParams httpParameters = httppost.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
            HttpConnectionParams.setSoTimeout(httpParameters, 3000);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                String responseBody = EntityUtils.toString(response.getEntity());
                httpResponse = responseBody;
                return !httpResponse.substring(httpResponse.length()-3, httpResponse.length()).equals(ERR);
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                httpResponse = statusLine.getReasonPhrase();
                return false;
            }
        }
        catch(IOException e){
            httpResponse = e.getMessage();
            return false;
        }
    }
    
    public String getHttpResponse(){
        return httpResponse;
    }
    
    private boolean isOnline(Activity a) {
        ConnectivityManager cm = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
