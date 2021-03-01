package com.zoomphant.agent.trace.common.minimal.utils;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     *
     * add a classloader to make sure loading the file under the correct jars.
     * eg: common-minal (this class) is loaded by bootstrap.
     * but jmx-base is loaded by another stand alone classloader.
     * if not same, it will throw file not found.
     * @param cl
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFile(ClassLoader cl, String file) throws IOException {

        // The class loader that loaded the class
        try (InputStream inputStream = cl.getResourceAsStream(file);) {
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
