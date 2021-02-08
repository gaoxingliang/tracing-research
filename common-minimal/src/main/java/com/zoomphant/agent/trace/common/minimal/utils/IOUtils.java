package com.zoomphant.agent.trace.common.minimal.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

public class IOUtils {
    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            }
            catch (IOException e) {
            }
        }
    }
    public static void close(HttpURLConnection c) {
        if (c != null) {
            try {
                c.disconnect();
            }
            catch (Exception e) {
            }
        }
    }
}
