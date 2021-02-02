package com.zoomphant.agent.trace.common;

import lombok.Cleanup;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String getFile(String file) throws IOException  {
        // The class loader that loaded the class
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(file);

        // the stream holding the file content
        if (inputStream == null) {
            throw new FileNotFoundException("file not found! " + file);
        } else {
            return IOUtils.toString(inputStream, "UTF-8");
        }
    }
}
