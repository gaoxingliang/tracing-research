package com.zoomphant.agent.trace.common.minimal.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class OutputUtils {
    public static byte[] toBytes(Object o) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            return bos.toByteArray();
        } finally {
            IOUtils.close(bos);
            IOUtils.close(oos);
        }
    }

    public static  <T> T toObject(byte[] bytes, Class<T> clazz) throws IOException {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
             bis = new ByteArrayInputStream(bytes);
             ois = new ObjectInputStream(bis);
            try {
                return (T) ois.readObject();
            }
            catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        } finally {
            IOUtils.close(bis);
            IOUtils.close(ois);
        }
    }
}
