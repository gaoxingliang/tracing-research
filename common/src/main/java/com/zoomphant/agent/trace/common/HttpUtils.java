package com.zoomphant.agent.trace.common;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpUtils {
    private static final OkHttpClient client = new OkHttpClient.Builder().callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).build();

    public static void post(String url, byte[] body, Map<String, String> headers) throws IOException {
        Request.Builder b = new Request.Builder().url(url).post(RequestBody.create(body));
        if (headers != null) {
            headers.forEach((k, v) -> b.header(k, v));
        }
        Call c = client.newCall(b.build());
        Response r = null;
        try {
            r = c.execute();
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    public static void post(String url, String body, Map<String, String> headers) throws IOException {
        post(url, body.getBytes(), headers);
    }
}
