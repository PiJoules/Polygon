/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.test;

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
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Pi_Joules
 */
public class HTTPManager {
    public HTTPManager(){
        try{
            HttpClient httpclient = new DefaultHttpClient();
            
            HttpPost httppost = new HttpPost("http://www.kompactit.com/polygon/scores.php");
            
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("first", " 'n'"));
            nameValuePairs.add(new BasicNameValuePair("last", " butts"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            
            //HttpResponse response = httpclient.execute(new HttpGet("http://www.kompactit.com/polygon/scores.php?first='y'&last=' butts'"));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }catch(IOException e){}
    }
}
