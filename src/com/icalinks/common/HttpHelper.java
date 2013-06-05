package com.icalinks.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHelper
{
    /**
     * 
     * 获取HTTP请求响应的字符串
     * 
     * @param strurl
     * @return
     **/
    public static String getHttpResponse(String strurl) {
        StringBuffer strResult = new StringBuffer();

        URL url = null;
        try {
            url = new URL(strurl);
        } catch (MalformedURLException e) {
            return null;
        }

        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());

            // 输出对象
            BufferedReader bufferedReader = new BufferedReader(in);

            String strInput = null;
            while ((strInput = bufferedReader.readLine()) != null) {
                strResult.append(strInput);
            }
        } catch (IOException e) {
            return null;
        }
        return strResult.toString();
    }
}
