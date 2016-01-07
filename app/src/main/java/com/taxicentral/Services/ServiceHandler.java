package com.taxicentral.Services;

/**
 * Created by MMF-an-new on 20-Aug-15.
 */

import android.content.Entity;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method,
                                  JSONObject params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(params);
                    StringEntity se = null;
                    try {
                        se = new StringEntity(jsonArray.toString(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d("Request Json ", jsonArray.toString());

                    httpPost.setEntity(se);
                    //httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if (method == GET) {
                // appending params to url
                if (params != null) {

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(params);
                    StringEntity se = null;
                    try {
                        se = new StringEntity(jsonArray.toString(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                   // Log.d("Sent Json : ", jsonArray.toString());

                    url += "?" + se;

                  //  String paramString = URLEncodedUtils.format(params, "utf-8");
                   // url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);
            }


            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            Log.d("Response Json : ", response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.d("Response Json : ", e.getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.d("Response Json : ", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Response Json : ", e.getMessage());
        }

        return response;

    }
}