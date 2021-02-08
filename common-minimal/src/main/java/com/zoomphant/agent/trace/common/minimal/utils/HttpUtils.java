package com.zoomphant.agent.trace.common.minimal.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtils {


    public static int post(String urlString, byte[] body, Map<String, String> headers) throws IOException {
        URL url = new URL (urlString);
        HttpURLConnection con = null;
        OutputStream os = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            final HttpURLConnection f = con;
            if (headers != null) {
                headers.forEach((k, v) -> f.addRequestProperty(k, v));
            }
            con.setDoOutput(true);
            os = con.getOutputStream();
            os.write(body, 0, body.length);
            os.flush();
            os.close();
            return con.getResponseCode();
        } finally {
            IOUtils.close(os);
            IOUtils.close(con);
        }


    }

    public static void post(String url, String body, Map<String, String> headers) throws IOException {
        post(url, body.getBytes(), headers);
    }
}
