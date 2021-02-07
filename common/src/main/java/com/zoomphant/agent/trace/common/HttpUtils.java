package com.zoomphant.agent.trace.common;

import okhttp3.OkHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).build();


    public static void post(String urlString, byte[] body, Map<String, String> headers) throws IOException {
        URL url = new URL (urlString);
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            final HttpURLConnection f = con;
            if (headers != null) {
                headers.forEach((k, v) -> f.addRequestProperty(k, v));
            }
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(body, 0, body.length);
        } finally {
            IOUtils.close(con);
        }


    }

    public static void post(String url, String body, Map<String, String> headers) throws IOException {
        post(url, body.getBytes(), headers);
    }
}
