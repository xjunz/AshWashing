package com.ashwashing.pro.util;

import android.util.Log;

import com.ashwashing.pro.AshApp;

import org.apaches.commons.codec.DecoderException;
import org.apaches.commons.codec.binary.Hex;
import org.apaches.commons.codec.digest.DigestUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class IOUtils {

    private static void transferStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int count;
        while ((count = in.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, count);
        }
        out.flush();
        in.close();
        out.close();
    }


    public static void serializeToStorage(Object obj, String path) {

        try {
            FileOutputStream fos = new FileOutputStream(path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.close();
            byte[] bytes = bos.toByteArray();
            String hex = String.valueOf(Hex.encodeHex(bytes));
            bos.close();
            ByteArrayInputStream bis = new ByteArrayInputStream(hex.getBytes());
            transferStream(bis, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readStackTrace(Throwable e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(out);
        e.printStackTrace(printStream);
        try {
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        printStream.close();
        return new String(out.toByteArray());
    }

    public static <T> T deserializeFromStorage(String path, Class<T> t) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            transferStream(fis, bos);
            byte[] decoded = Hex.decodeHex(bos.toString());
            ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
            ObjectInputStream ois = new ObjectInputStream(bis);
            bis.close();
            ois.close();
            try {
                return t.cast(ois.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeLog(String fileName, String log) {
        File file = new File(AshApp.LOG_DIR + fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            log = UniUtils.getCurrentDateAndTime() + "\n" + log;
            writer.write(log, 0, log.length());
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeErrorLog(Throwable t) {
        String dnt = UniUtils.getCurrentDateAndTime();
        String log = dnt
                + "\n" + UniUtils.getVersionInfo()
                + "\n" + UniUtils.getEssentialHardwareInfo()
                + "\n" + Log.getStackTraceString(t);
        byte[] encrypted = log.getBytes();
        File file = new File(AshApp.LOG_DIR + dnt + ".log");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(encrypted, 0, encrypted.length);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
