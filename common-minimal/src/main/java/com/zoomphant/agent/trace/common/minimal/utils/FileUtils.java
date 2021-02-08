package com.zoomphant.agent.trace.common.minimal.utils;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String getFile(String file) throws IOException {

        // The class loader that loaded the class
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(file)) {
            // the stream holding the file content
            if (inputStream == null) {
                throw new FileNotFoundException("file not found! " + file);
            }
            else {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte [] buf = new byte[1024];
                int n;
                while ((n = inputStream.read(buf)) > 0) {
                    bos.write(buf, 0, n);
                }
                return new String(bos.toByteArray());
            }
        }
    }
}
