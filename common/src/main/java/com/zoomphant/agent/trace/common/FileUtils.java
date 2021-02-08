package com.zoomphant.agent.trace.common;

import lombok.Cleanup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String getFile(String file) throws IOException {
        // The class loader that loaded the class
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(file);

        // the stream holding the file content
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + file);
        }
        else {
            byte[] max = new byte[1024 * 1024 * 4];
            int n = inputStream.read(max);
            return new String(max, 0, n);
        }
    }
}
