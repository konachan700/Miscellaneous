package ru.mew_hpm.gpontools_v3.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ru.mew_hpm.gpontools_v3.dao.SSHCommand;

public class AppUtils {
    public static final String InputStreamToString(InputStream stream) {
        final InputStreamReader ir = new InputStreamReader(stream);
        final BufferedReader br = new BufferedReader(ir);
        final StringBuilder scriptSB = new StringBuilder();

        String line;

        try {
            while ((line = br.readLine()) != null) {
                scriptSB.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ir.close();
            br.close();
        } catch (IOException e) { }

        return scriptSB.toString();
    }

    public static String ReaderToStringDNU(Reader readerIn, String shellPrefix, SSHCommand cmdExecText) {
        final StringBuilder inStr = new StringBuilder();
        final StringBuilder inLine = new StringBuilder();
        int inByte;
        while (true) {
            try {
                inByte = readerIn.read();
                if ((inByte >= 0) && (inByte <= 255)) {
                    if (inByte == '\n') {
                        if (!inLine.toString().trim().contentEquals(cmdExecText.getCommand())) {
                            Log.d("### SSH DATA ###", inLine.toString());
                            inStr.append(inLine).append('\n');
                        } else
                            Log.d("### SSH ECHO ###", inLine.toString());
                        inLine.delete(0, inLine.length());
                    } else {
                        if (inLine.toString().matches(shellPrefix+"(.*)#(.*)")) {
                            Log.d("### SSH MARK ###", inLine.toString());
                            break;
                        }
                        inLine.append((char) inByte);
                    }
                }
            } catch (IOException e) { break; }
        }
        return inStr.toString();
    }

    public static String ReaderToStringDNU(Reader readerIn, String shellPrefix) {
        final StringBuilder inStr = new StringBuilder();
        final StringBuilder inLine = new StringBuilder();
        int inByte;
        while (true) {
            try {
                inByte = readerIn.read();
                if ((inByte >= 0) && (inByte <= 255)) {
                    if (inByte == '\n') {
                        Log.d("### SSH DATA ###", inLine.toString());
                        inStr.append(inLine).append('\n');
                        inLine.delete(0, inLine.length());
                    } else {
                        if (inLine.toString().matches(shellPrefix+"(.*)#(.*)")) {
                            Log.d("### SSH MARK ###", inLine.toString());
                            break;
                        }
                        inLine.append((char) inByte);
                    }
                }
            } catch (IOException e) { break; }
        }
        return inStr.toString();
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
