package com.mz.commons.sys;

import java.io.*;

/**
 * 2016-12-06
 * 第三次修改
 */
public class EncodingStream extends Thread {
    private InputStream is;
    private OutputStream os;

    public EncodingStream(InputStream is, OutputStream redirect) {
        this.is = is;
        this.os = redirect;
    }

    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                }
                System.out.println(">>" + line);
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
