package com.paypal.sea.s2dbservices;

import java.io.IOException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyLogger {
    private static MyLogger mObj = null;
    private Logger mLogger;

    // private static boolean firstThread = true;

    protected MyLogger() {
        mLogger = Logger.getLogger("MyLog");

        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter

            loadLogProperties();
            int filesize = Integer.valueOf(
                    getLogProperty("java.util.logging.FileHandler.limit"))
                    .intValue();
            int fileCount = Integer.valueOf(
                    getLogProperty("java.util.logging.FileHandler.count"))
                    .intValue();

            fh = new FileHandler("s2dbservices.log", filesize, fileCount, true);
            mLogger.addHandler(fh);
            mLogger.setLevel(Level.ALL);
            MyFormatter formatter = new MyFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages
            mLogger.log(Level.WARNING, "My first log");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getLogProperty(String key) {
        LogManager lm = LogManager.getLogManager();
        String value = lm.getProperty(key);
        if (value == null) {
            value = "";
        }
        return value;
    }

    private void loadLogProperties() {
        String file = System.getProperty("java.util.logging.config.file");

        if (file == null || file.isEmpty()) {
            URL url = null;
            try {
                url = MyLogger.class.getClassLoader().getResource(
                        "log.properties");

                if (url != null) {
                    LogManager.getLogManager().readConfiguration(
                            url.openStream());
                }
            } catch (Exception e) {

            }
        }
    }

    public void log(java.util.logging.Level level, String msg) {
        mLogger.log(level, msg);
    }

    public static MyLogger getInstance() {
        if (mObj == null) {
            synchronized (MyLogger.class) {
                if (mObj == null) {
                    mObj = new MyLogger();
                }
            }
        }
        return mObj;
    }
}