package com.mooreb.config.common;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

public class EncodingUtils {
    public static byte[] myEncode(Map<String,String> params) throws IOException {
        final StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        return postData.toString().getBytes("UTF-8");
    }
}
